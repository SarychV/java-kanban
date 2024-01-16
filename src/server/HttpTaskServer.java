package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/* JSON строки для тестирования API
{"taskId":0,"title":"simple","description":"simple description","status":"NEW","duration":23,"startTime":"2023-06-30T22:17"}
{"subtasks":[],"endTime":null,"taskId":0,"title":"epic","description":"epic description","status":"NEW","duration":0,"startTime":null}
{"parentEpicId":3,"taskId":0,"title":"title","description":"description","status":"NEW","duration":10,"startTime":"2023-06-30T22:03"}
 */

public class HttpTaskServer {
    static TaskManager taskManager = Managers.getDefault();
    public static final int PORT = 8080;
    HttpServer apiServer;

    public HttpTaskServer() throws IOException {
        apiServer = HttpServer.create();
        apiServer.bind(new InetSocketAddress(PORT), 0);
        apiServer.createContext("/tasks/", new TasksHandler());
    }

    public void start() {
        apiServer.start();
        System.out.println("API сервер запущен на порту " + PORT);
    }

    public static void main(String... unused) throws IOException {
        new KVServer().start();
        new HttpTaskServer().start();
    }

    static class TasksHandler implements HttpHandler  {
        private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
        private HttpExchange exchange;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            TaskType taskType;

            int taskId = -1;
            /* Это начальное значение, означающее, что id использовать не надо.
            У меня в коде это некорректное значение id (используется везде).
            Проще говоря, это значение, что сервером taskId не получен.
            Если значение получено, то taskId будет целым положительным числом. */

            String requestMethod;
            String taskJson;

            try {
                this.exchange = exchange;
                URI requestUri = exchange.getRequestURI();

                Optional<TaskType> typeInPath = findOutTaskType(requestUri.getPath());
                if (typeInPath.isPresent()) {
                    taskType = typeInPath.get();
                } else {
                    exchange.sendResponseHeaders(501, -1);
                    return;
                }

                Optional<Integer> idInQuery = findOutTaskId(requestUri.getQuery());
                if (idInQuery.isPresent()) {
                    taskId = idInQuery.get();
                }

                requestMethod = exchange.getRequestMethod();

                InputStream is = exchange.getRequestBody();
                taskJson = new String(is.readAllBytes(), DEFAULT_CHARSET);

                System.out.println(requestMethod + ", " + taskType + ", " + taskId
                        + "\nTask:" + taskJson);

                processRequest(requestMethod, taskType, taskId, taskJson);
            } finally {
                exchange.close();
            }
        }

        Optional<TaskType> findOutTaskType(String path) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 2) return Optional.of(TaskType.ALL); // /tasks/
            else if (pathParts.length == 3) {
                if ("epic".equals(pathParts[2])) return Optional.of(TaskType.EPIC); // /tasks/epic
                if ("task".equals(pathParts[2])) return Optional.of(TaskType.TASK); // /tasks/task
                if ("subtask".equals(pathParts[2])) return Optional.of(TaskType.SUBTASK); // /tasks/subtask
                if ("history".equals(pathParts[2])) return Optional.of(TaskType.HISTORY); // /tasks/history
            }
            return Optional.empty();
        }

        Optional<Integer> findOutTaskId(String query) {
            if (query != null) {
                String[] queryParts = query.split("=");
                if (queryParts.length == 2) {
                    if (queryParts[0].equalsIgnoreCase("id")) {
                        if (queryParts[1].matches("^\\d+$")) {
                            return Optional.of(Integer.parseInt(queryParts[1]));
                        }
                    }
                }
            }
            return Optional.empty();
        }

        // должна возвращать код состояния для клиента
        void processRequest(String method, TaskType taskType, int taskId, String taskJson) {
            switch (method) {
                case "GET":
                    List<Task> tasks = getTasksFromManager(taskType, taskId);
                    sendTasksToClient(tasks);
                    break;
                case "POST":
                    addOrChangeTaskInManager(taskType, taskJson);
                    break;
                case "DELETE":
                    removeTasksInManager(taskType, taskId);
                    break;
                default:
            }
        }

        List<Task> getTasksFromManager(TaskType taskType, int taskId) {
            List<Task> tasks = new LinkedList<>();
            switch (taskType) {
                case ALL:
                    if (taskId <= 0) {
                        tasks.addAll(taskManager.getAllSimpleTasks());
                        tasks.addAll(taskManager.getAllEpicTasks());
                        tasks.addAll(taskManager.getAllSubtasks());
                    } else {
                        tasks.add(taskManager.getTask(taskId));
                    }
                    break;
                case TASK:
                    if (taskId <= 0) {
                        tasks.addAll(taskManager.getAllSimpleTasks());
                    } else {
                        tasks.add(taskManager.getSimpleTask(taskId));
                    }
                    break;
                case EPIC:
                    if (taskId <= 0) {
                        tasks.addAll(taskManager.getAllEpicTasks());
                    } else {
                        tasks.add(taskManager.getEpicTask(taskId));
                    }
                    break;
                case SUBTASK:
                    if (taskId <= 0) {
                        tasks.addAll(taskManager.getAllSubtasks());
                    } else {
                        tasks.add(taskManager.getSubtask(taskId));
                    }
                    break;
                case HISTORY:
                    tasks.addAll(taskManager.getHistory());
                default:
                    break;
            }
            return tasks;
        }

        void sendTasksToClient(List<Task> tasks) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
            Gson gson = gsonBuilder.create();

            try (OutputStream os = this.exchange.getResponseBody()) {
                this.exchange.sendResponseHeaders(200, 0);
                for (Task task : tasks) {
                    String taskJson = gson.toJson(task);
                    os.write(taskJson.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void addOrChangeTaskInManager(TaskType taskType, String taskJson) {
            Optional<Task> taskFromJson = getTaskFromJson(taskType, taskJson);
            if (taskFromJson.isPresent()) {
                Task task = taskFromJson.get();
                if (task.getId() <= 0) {
                    taskManager.addTask(task);
                } else {
                    taskManager.updateTask(task);
                }
            } else {
                // Направить клиенту код статуса о неудачной обработке задачи
                // В частности, может быть неверным taskType или задача в формате JSON содержит неверные поля.
            }
            try {
                this.exchange.sendResponseHeaders(200, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Optional<Task> getTaskFromJson(TaskType taskType, String taskJson) {
            Task task = null;

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
            Gson gson = gsonBuilder.create();

            try {
                switch (taskType) {
                    case TASK:
                        task = gson.fromJson(taskJson, SimpleTask.class);
                        break;
                    case EPIC:
                        task = gson.fromJson(taskJson, EpicTask.class);
                        break;
                    case SUBTASK:
                        task = gson.fromJson(taskJson, Subtask.class);
                        break;
                    default:
                        System.out.println("getTaskFromJson(): Недопустимый тип задачи.");
                        break;
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                try {
                    this.exchange.sendResponseHeaders(415, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return Optional.ofNullable(task);
        }


        void removeTasksInManager(TaskType taskType, int taskId) {
            switch (taskType) {
                case TASK:
                    if (taskId <= 0) {
                        taskManager.removeAllSimpleTasks();
                    } else {
                        taskManager.removeSimpleTask(taskId);
                    }
                case EPIC:
                    if (taskId <= 0) {
                        taskManager.removeAllEpicTasks();
                    } else {
                        taskManager.removeEpicTask(taskId);
                    }
                case SUBTASK:
                    if (taskId <= 0) {
                        taskManager.removeAllSubtasks();
                    } else {
                        taskManager.removeSubtask(taskId);
                    }
                case ALL:
                    if (taskId <= 0) {
                        taskManager.getAllSimpleTasks();
                        taskManager.removeAllEpicTasks();
                        taskManager.removeAllSubtasks();
                    } else {
                        taskManager.removeTask(taskManager.getTask(taskId));
                    }
            }
            try {
                this.exchange.sendResponseHeaders(200, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(localDate.toString());
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        return LocalDateTime.parse(jsonReader.nextString());
    }
}
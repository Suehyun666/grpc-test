package com.gload.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/api/logs")
@Produces(MediaType.APPLICATION_JSON)
public class LogController {

    @GET
    public List<Map<String, Object>> listLogs() {
        File logsDir = new File("logs");
        if (!logsDir.exists() || !logsDir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = logsDir.listFiles((dir, name) -> name.endsWith(".jsonl"));
        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
            .map(file -> {
                Map<String, Object> info = new HashMap<>();
                info.put("name", file.getName());
                info.put("size", file.length());
                info.put("lastModified", file.lastModified());
                return info;
            })
            .sorted((a, b) -> Long.compare((Long) b.get("lastModified"), (Long) a.get("lastModified")))
            .collect(Collectors.toList());
    }

    @GET
    @Path("/{filename}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLogContent(@PathParam("filename") String filename,
                                @QueryParam("tail") @DefaultValue("100") int tail) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("logs", filename));

            if (tail > 0 && lines.size() > tail) {
                lines = lines.subList(lines.size() - tail, lines.size());
            }

            return String.join("\n", lines);
        } catch (IOException e) {
            throw new WebApplicationException("Failed to read log file: " + e.getMessage(), 500);
        }
    }
}

package fr.pantheonsorbonne.ufr27;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo
        extends AbstractMojo {



    @Parameter(defaultValue = "src/main/java", property = "sourceDir")
    private File sourceDir;

    @Parameter(defaultValue = "dummy", property = "userName")
    private String userName;

    public void execute()
            throws MojoExecutionException {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("https://event-sink.miage.dev"));
        EventResource proxy = target.proxy(EventResource.class);
        String currentWorkingDirectory = System.getProperty("user.dir");
        Map<String, String> files = new HashMap<>();
        try (Stream<Path> stream = Files.walk(Path.of(sourceDir.toString()))) {
            // Filter and print each file
            files = stream
                    .filter(Files::isRegularFile) // Only include regular files (not directories)
                    .map(f -> {
                        try {
                            String fileContent = Files.readString(f);
                            return Map.entry(f.getFileName().toString(), fileContent);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, files);
            proxy.postEventUnsafe(new EventDTO("miage-maven-plugin",
                    "miage-maven-plugin",
                    userName,
                    "code-built",
                    writer.toString()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

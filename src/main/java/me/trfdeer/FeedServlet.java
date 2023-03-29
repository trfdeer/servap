package me.trfdeer;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.trfdeer.data.DataStore;
import me.trfdeer.model.InstantSerializer;
import me.trfdeer.model.Link;
import me.trfdeer.model.Response;
import me.trfdeer.model.Source;

public class FeedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int sourceId = Integer.parseInt(req.getParameter("source"));
            int refresh = Integer.parseInt(req.getParameter("refresh"));

            DataStore ds = new DataStore();

            Source src = ds.getSource(sourceId);
            URL feedUrl = new URL(src.getUrl());

            if (refresh == 1) {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));
                List<SyndEntry> entries = feed.getEntries();
                List<Link> links = entries.stream().map(entry -> {
                    String title = entry.getTitle();
                    String description = "<br />"
                            + (entry.getDescription() == null ? "" : entry.getDescription().getValue());
                    description += String.join("<br />",
                            entry.getContents().stream()
                                    .map(content -> content.getValue() == null ? "" : content.getValue())
                                    .filter(it -> !it.contains("SyndContentImpl") && !it.isBlank())
                                    .toList());
                    Instant publishedDate = entry.getPublishedDate().toInstant();
                    String author = entry.getAuthor();
                    String link = entry.getLink();
                    return new Link(-1, sourceId, title, link, description, author, publishedDate);
                }).toList();

                ds.addLinks(links);
            }

            List<Link> links = ds.getLinks(sourceId);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Instant.class, new InstantSerializer());
            Gson gson = builder.create();

            Response<List<Link>> response = new Response<>(false, "", links);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(200);
            resp.getWriter().write(responseJson);

        } catch (NumberFormatException e) {
            String message = "Invalid Source ID: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().write(responseJson);
        } catch (Exception e) {
            String message = "Failed to Get links: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(500);
            resp.getWriter().write(responseJson);
        }
    }

}

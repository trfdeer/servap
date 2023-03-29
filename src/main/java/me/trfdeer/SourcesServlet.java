package me.trfdeer;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.trfdeer.data.DataStore;
import me.trfdeer.model.Response;
import me.trfdeer.model.Source;
import me.trfdeer.model.SourceResponse;

public class SourcesServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("feed_id"));
            DataStore ds = new DataStore();

            int res = ds.deleteSource(id);
            if (res == -1) {
                throw new Exception("Something Went wrong");
            }

            Gson gson = new Gson();
            Response<Integer> response = new Response<>(false, "Deleted source.", res);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(200);
            resp.getWriter().write(responseJson);
        } catch (Exception e) {
            String message = "Failed to delete source: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(500);
            resp.getWriter().write(responseJson);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DataStore ds = new DataStore();
            List<Source> sources = ds.getAllSources();
            List<String> directories = ds.getAllDirectories();
            SourceResponse respData = new SourceResponse(sources, directories);

            Gson gson = new Gson();
            Response<SourceResponse> response = new Response<>(false, "OK", respData);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(200);
            resp.getWriter().write(responseJson);
        } catch (Exception e) {
            String message = "Failed to get sources: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(500);
            resp.getWriter().write(responseJson);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String title = req.getParameter("feed_title");
            String url = req.getParameter("feed_url");
            String folder = req.getParameter("feed_folder");

            DataStore ds = new DataStore();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(url)));

            String faviconUrl = "https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&size=256&url="
                    + (feed.getLink() == null ? "http://trfdeer.me" : feed.getLink());
            int res = ds.addSource(folder, title, url, faviconUrl);
            if (res == -1) {
                throw new Exception("Something Went wrong");
            }

            Gson gson = new Gson();
            Response<Integer> response = new Response<>(false, "Added new source.", res);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(201);
            resp.getWriter().write(responseJson);
        } catch (Exception e) {
            String message = "Failed to add source: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(500);
            resp.getWriter().write(responseJson);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("feed_id"));
            String title = req.getParameter("new_feed_title");
            String url = req.getParameter("new_feed_url");
            String folder = req.getParameter("new_feed_folder");

            DataStore ds = new DataStore();

            int res = ds.updateSource(id, title, url, folder);
            if (res == -1) {
                throw new Exception("Something Went wrong");
            }

            Gson gson = new Gson();
            Response<Integer> response = new Response<>(false, "Updated source.", res);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(200);
            resp.getWriter().write(responseJson);
        } catch (Exception e) {
            String message = "Failed to update source: " + e.getMessage();

            Gson gson = new Gson();
            Response<Object> response = new Response<>(true, message, null);
            String responseJson = gson.toJson(response);

            resp.setContentType("application/json");
            resp.setStatus(500);
            resp.getWriter().write(responseJson);
        }
    }

}
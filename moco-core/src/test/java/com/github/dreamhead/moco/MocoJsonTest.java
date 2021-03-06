package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonTest extends AbstractMocoHttpTest {
	@Test
	public void should_return_content_based_on_jsonpath() throws Exception {
		server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
		running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"), 
                        is("jsonpath match success"));
            }
        });
	}

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_jsonpath() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{\"book\":{\"price\":\"2\"}}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_path_found() throws Exception {
        server.request(eq(jsonPath("anything"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_found() throws Exception {
        server.request(eq(jsonPath("$.book.price"), "1")).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "{}");
            }
        });
    }

    @Test
    public void should_match_exact_json() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(json(jsonContent)).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_exact_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(json(text(jsonContent))).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), jsonContent), is("foo"));
            }
        });
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        server.request(json("{\"foo\":\"bar\"}")).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\n\t\"foo\":\"bar\"\n}"), is("foo"));
            }
        });
    }

    @Test
    public void should_return_content_based_on_jsonpath_existing() throws Exception {
        server.request(exist(jsonPath("$.book.price"))).response("jsonpath match success");
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "{\"book\":{\"price\":\"1\"}}"),
                        is("jsonpath match success"));
            }
        });
    }
}

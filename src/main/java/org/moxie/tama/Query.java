package org.moxie.tama;

import org.apache.commons.io.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: blangel
 * Date: 6/24/11
 * Time: 11:02 AM
 */
public interface Query {

    static class Default implements Query {

        protected final URL queryUrl;

        public Default(String queryUrl) {
            try {
                this.queryUrl = new URL(queryUrl);
            } catch (MalformedURLException murle) {
                throw new RuntimeException(murle);
            }
        }

        @Override public String query() {
            try {
                URLConnection connection = queryUrl.openConnection();
                InputStream in = connection.getInputStream();
                String encoding = connection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                return IOUtil.toString(in, encoding);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if ((o == null) || (getClass() != o.getClass())) {
                return false;
            }
            Default that = (Default) o;
            return (queryUrl == null ? (that.queryUrl == null) : (queryUrl.equals(that.queryUrl)));
        }

        @Override public int hashCode() {
            return (queryUrl == null ? 0 : queryUrl.hashCode());
        }
    }

    String query();

}

package it.tika;

import com.goal98.tika.common.Paragraphs;
import flipdroid.grepper.GrepperException;
import flipdroid.grepper.pipe.PipeContentExtractor;
import it.tika.blacklist.BlacklistedImageDBMongoDB;
import it.tika.blacklist.BlacklistedTikaImage;
import it.tika.exception.ExtractorException;
import it.tika.image.ImageDBMongoDB;
import it.tika.image.TikaImage;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ContentExtractor implements Extractor {
    PipeContentExtractor extractor;

    public void setExtractor(PipeContentExtractor extractor) {
        this.extractor = extractor;
    }

    public void extract(URLAbstract urlAbstract) {
        try {
            String content = extractor.fireAbstract(urlAbstract.getRawContent(), urlAbstract.getCharset());
            urlAbstract.setContent(content);
            if (urlAbstract.getTitle() == null || urlAbstract.getTitle().length() == 0)//in case title fetcher failed
                urlAbstract.setTitle(extractor.getTitle());
            URL url = null;
            try {
                url = new URL(urlAbstract.getUrl());
            } catch (MalformedURLException e) {

            }

            int largestAreaIndex = -1;
            int largestArea = -1;
            int index = 0;
            Iterator<String> imagesIterator = extractor.getImages().iterator();
            List<String> filteredImages = new ArrayList<String>();
            while (imagesIterator.hasNext()) {
                String imageURL = imagesIterator.next().trim();
                char[] chars = imageURL.toCharArray();
                int firstChar = 0;
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] <= 127) {
                        firstChar = i;
                        break;
                    }
                }
                imageURL = imageURL.substring(firstChar);
                int width = 0;
                int height = 0;
                if (imageURL.indexOf("#") == -1) {
                    continue;
                }
                if (!imageURL.startsWith("http")) {
                    if (imageURL.startsWith("/")) {
                        imageURL = url.getProtocol() + "://" + url.getHost() + imageURL;
                    } else {
                        String upOneLevel = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
                        imageURL = url.getProtocol() + "://" + url.getHost() + upOneLevel + imageURL;
                    }
                }
                int hashIndex = imageURL.lastIndexOf("#");
                String queryURL = imageURL.substring(0, hashIndex);
                int first = queryURL.indexOf("http");
                queryURL = queryURL.substring(first);
                try {
                    BlacklistedTikaImage blacklistedTikaImage = BlacklistedImageDBMongoDB.getInstance().find(queryURL);
                    if (blacklistedTikaImage != null)
                        continue;
                } catch (Exception e) {

                }


                ImageInfo ii = null;
                TikaImage tikaImage;
                ImageDBMongoDB dbInstance = null;
                try {
                    dbInstance = ImageDBMongoDB.getInstance();
                    tikaImage = dbInstance.find(queryURL);
                } catch (Exception e) {
                    tikaImage = null;
                }
                if (tikaImage != null) {
                    ii = new ImageInfo();
                    ii.setSize(tikaImage.getSize());
                    ii.setHeight(tikaImage.getHeight());
                    ii.setWidth(tikaImage.getWidth());
                    System.out.println("From Image DB Cache....");
                } else {
                    String dimensionInfo = imageURL.substring(hashIndex, imageURL.length());
                    if ("#,".equals(dimensionInfo)) {
                        width = 0;
                        height = 0;
                    } else {
                        dimensionInfo = dimensionInfo.substring(1);
                        String[] dimension = dimensionInfo.split(",");
                        if (dimension.length == 1) {
                            try {
                                width = Integer.valueOf(dimension[0]);
                            } catch (Exception e) {
                                width = 0;
                            }
                            height = 0;
                        } else if (dimensionInfo.startsWith(",")) {
                            width = 0;
                            try {
                                height = Integer.valueOf(dimension[1]);
                            } catch (Exception e) {
                                height = 0;
                            }
                        } else {
                            try {
                                width = Integer.valueOf(dimension[0]);
                            } catch (Exception e) {
                                width = 0;
                            }
                            try {
                                height = Integer.valueOf(dimension[1]);
                            } catch (Exception e) {
                                height = 0;
                            }
                        }
                    }


                    if (width == 0 || height == 0) {
                        try {
                            ii = getImageInfo(queryURL);
                        } catch (IOException e) {
                            imagesIterator.remove();
                            index++;
                            continue;
                        }
                    } else {
                        ii = new ImageInfo();
                        ii.setWidth(width);
                        ii.setHeight(height);
                        try {
                            ii.setSize(getFileSize(new URL(queryURL)));
                        } catch (IOException e) {
                            imagesIterator.remove();
                            index++;
                            continue;
                        }
                    }
                }
                final int fileSize = ii.getSize();

                TikaImage newImage = new TikaImage();
                newImage.setUrl(queryURL);
                newImage.setSize(fileSize);
                newImage.setHeight(ii.getHeight());
                newImage.setWidth(ii.getWidth());
                try {
                    if (dbInstance != null)
                        dbInstance.insert(newImage);
                } catch (Exception e) {

                }
                if (height * width < 2500) {
                    imagesIterator.remove();
                } else if (fileSize < 5000 && height < 130 && width < 130) {
                    imagesIterator.remove();
                } else {

                    filteredImages.add(queryURL);
                    final int fileArea = ii.getWidth() * ii.getHeight();
                    if (fileArea > largestArea) {
                        largestArea = fileArea;
                        largestAreaIndex = filteredImages.size() - 1;
                    }
                }
                index++;
            }
            if (largestAreaIndex >= 0 && filteredImages.size() > largestAreaIndex)
                Collections.swap(filteredImages, 0, largestAreaIndex);

            Paragraphs paragraphs = new Paragraphs();
            paragraphs.toParagraph(content);
            paragraphs.retain(filteredImages);
            urlAbstract.setContent(paragraphs.toContent());
            urlAbstract.setImages(filteredImages);
        } catch (GrepperException e) {
            throw new ExtractorException(e);
        } catch (Exception e) {
            throw new ExtractorException(e);
        }
    }

    private ImageInfo getImageInfo(String imageURL) throws IOException {
        URL touchingImageURL = new URL(imageURL);
        HttpURLConnection httpConnection = (HttpURLConnection) (touchingImageURL
                .openConnection());
        httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode < 200 || responseCode > 299) {
            ImageInfo ii = new InvalidImageInfo();
            return ii;
        }
        InputStream is = httpConnection.getInputStream();

        byte[] image = IOUtils.toByteArray(is);
        ImageInfo ii = new ImageInfo();

        ii.setSize(image.length);
        BufferedImage bi = null;
        bi = javax.imageio.ImageIO.read(new ByteArrayInputStream(image));
        if (bi != null) {
            ii.setWidth(bi.getWidth());
            ii.setHeight(bi.getHeight());
        }

        return ii;

    }

    private int getFileSize(URL url) {
        int fileLength = -1;
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) (url
                    .openConnection());
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode < 200 || responseCode > 299) {
                return -1;
            }
            String sHeader;
            for (int i = 1; ; i++) {
                sHeader = httpConnection.getHeaderFieldKey(i);
                if (sHeader != null) {
                    if (sHeader.equals("Content-Length")) {
                        fileLength = Integer.parseInt(httpConnection
                                .getHeaderField(sHeader));
                        break;
                    }
                } else {
                    break;
                }
            }
            if (fileLength == -1) {
                InputStream is = httpConnection.getInputStream();
                int i;
                int sum = 0;
                byte[] bytes = new byte[1024];
                while ((i = is.read(bytes)) != -1) {
                    sum += i;
                    bytes = new byte[1024];
                }
                return sum;
            }
        } catch (Exception ex) {
            return -1;
        }

        return fileLength;
    }

    private class ImageInfo {
        private int size;
        private int width;
        private int height;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    private class InvalidImageInfo extends ImageInfo {
        public int getSize() {
            return -1;
        }


        public int getWidth() {
            return -1;
        }

        public int getHeight() {
            return -1;
        }
    }
}

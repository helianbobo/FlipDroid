package it.tika;

import flipdroid.grepper.GrepperException;
import flipdroid.grepper.pipe.PipeContentExtractor;
import it.tika.exception.ExtractorException;
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
                String imageURL = imagesIterator.next();

                int width = 0;
                int height = 0;
                if (imageURL.indexOf("#") == -1) {
                    continue;
                }
                int hashIndex = imageURL.lastIndexOf("#");
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

                imageURL = imageURL.substring(0, hashIndex);
                if (imageURL.startsWith("/")) {
                    imageURL = url.getProtocol() + "://" + url.getHost() + imageURL;
                }
                ImageInfo ii = null;
                if (width == 0 || height == 0) {
                    try {
                        ii = getImageInfo(imageURL);
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
                        ii.setSize(getFileSize(new URL(imageURL)));
                    } catch (IOException e) {
                        imagesIterator.remove();
                        index++;
                        continue;
                    }
                }
                final long fileSize = ii.getSize();


                if (fileSize < 5000 && height < 130 && width < 130) {
                    imagesIterator.remove();
                } else {
                    filteredImages.add(imageURL);
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
        private long size;
        private int width;
        private int height;

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
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
        public long getSize() {
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

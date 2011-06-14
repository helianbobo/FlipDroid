package it.tika;

import flipdroid.grepper.GrepperException;
import flipdroid.grepper.pipe.PipeContentExtractor;
import it.tika.exception.ExtractorException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
            URL url = null;
            try {
                url = new URL(urlAbstract.getUrl());
            } catch (MalformedURLException e) {

            }

            Iterator<String> imagesIterator = extractor.getImages().iterator();
            List<String> filteredImages = new ArrayList<String>();
            while (imagesIterator.hasNext()) {
                String imageURL = imagesIterator.next();

                int width = 0;
                int height = 0;
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
                try {
                    URL touchingImageURL = new URL(imageURL);
                    if (getFileSize(touchingImageURL) < 7000) {
                        if (height < 130 && width < 130)
                            imagesIterator.remove();
                        else
                            filteredImages.add(imageURL);
                    } else {
                        filteredImages.add(imageURL);
                    }
                } catch (MalformedURLException e) {
                    imagesIterator.remove();
                } catch (Exception e) {

                }
            }

            urlAbstract.setImages(filteredImages);
        } catch (GrepperException e) {
            throw new ExtractorException(e);
        }
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
}

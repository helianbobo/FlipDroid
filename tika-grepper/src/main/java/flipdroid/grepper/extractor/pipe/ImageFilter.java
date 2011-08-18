package flipdroid.grepper.extractor.pipe;

import com.goal98.tika.common.ImageInfo;
import com.goal98.tika.common.Paragraphs;
import flipdroid.grepper.URLAbstract;
import flipdroid.grepper.extractor.Extractor;
import it.tika.mongodb.image.ImageService;
import it.tika.mongodb.image.TikaImage;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ITS
 * Date: 11-8-8
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class ImageFilter implements Extractor {
    private ImageService tikaImageService;


    public void extract(URLAbstract urlAbstract) {
        filterImage(urlAbstract);
    }

    public ImageFilter(ImageService tikaImageService) {
        this.tikaImageService = tikaImageService;
    }

    private void filterImage(URLAbstract urlAbstract) {
        String htmlContent = new String(urlAbstract.getRawContent(), urlAbstract.getCharset());
        String baseURL = "";
        int pos = htmlContent.indexOf("<base href=\"");
        if (pos != -1) {
            pos += "<base href=\"".length();
            int pos2 = htmlContent.indexOf("\"", pos);
            baseURL = htmlContent.substring(pos, pos2).trim();
            if (baseURL.endsWith("/")) {
                baseURL = baseURL.substring(0, baseURL.length() - 1);
            }
        }


        URL url = null;
        try {
            url = new URL(urlAbstract.getUrl());
        } catch (MalformedURLException e) {

        }
        Map<String, ImageInfo> imageInfoMap = new HashMap<String, ImageInfo>();
        int largestAreaIndex = -1;
        int largestArea = -1;
        int index = 0;
        Iterator<String> imagesIterator = urlAbstract.getImages().iterator();
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
//                        String upOneLevel = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
                    if (baseURL.length() != 0) {
                        imageURL = baseURL + "/" + imageURL;
                    } else {
                        if (!imageURL.startsWith("/"))
                            imageURL = url.getProtocol() + "://" + url.getHost() + "/" + imageURL;
                        else
                            imageURL = url.getProtocol() + "://" + url.getHost() + imageURL;
                    }
                }
            }
            int hashIndex = imageURL.lastIndexOf("#");
            String queryURL = imageURL.substring(0, hashIndex);
            int first = queryURL.indexOf("http");
            queryURL = queryURL.substring(first);
            if (tikaImageService != null && tikaImageService.isBlacklisted(queryURL))
                continue;


            ImageInfo ii = null;
            TikaImage tikaImage = null;
            if (tikaImageService != null) {
                tikaImage = tikaImageService.getImageInfoFromDBCache(queryURL);
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
            newImage.setSize(ii.getSize());
            newImage.setHeight(ii.getHeight());
            newImage.setWidth(ii.getWidth());


            if (tikaImageService != null) {
                tikaImageService.cacheToDB(newImage);
            }
            height = ii.getHeight();
            width = ii.getWidth();
            if (height * width < 3000) {
                imagesIterator.remove();
            } else if (fileSize < 5000 && height < 130 && width < 130) {
                imagesIterator.remove();
            } else {
                if (height / width >= 9 || width / height >= 9)
                    imagesIterator.remove();
                else if (ii.getWidth() != 0 && ii.getHeight() != 0) {
                    imageInfoMap.put(queryURL, ii);
                    filteredImages.add(queryURL + "#" + ii.getWidth() + "," + ii.getHeight());
                    final int fileArea = ii.getWidth() * ii.getHeight();
                    if (fileArea > largestArea) {
                        largestArea = fileArea;
                        largestAreaIndex = filteredImages.size() - 1;
                    }
                } else
                    imagesIterator.remove();
            }
            index++;
        }
        if (largestAreaIndex >= 0 && filteredImages.size() > largestAreaIndex)
            Collections.swap(filteredImages, 0, largestAreaIndex);


        Paragraphs paragraphs = new Paragraphs();
        paragraphs.toParagraph(urlAbstract.getContent());
        paragraphs.retain(filteredImages, imageInfoMap);
        urlAbstract.setContent(paragraphs.toContent());
        urlAbstract.setImages(filteredImages);
        System.out.println("urlAbstract.getContent()" + urlAbstract.getContent());
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

        byte[] image;
        try {
            image = IOUtils.toByteArray(is);
        } finally {
            is.close();
        }
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

        InputStream is = null;
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) (url
                    .openConnection());
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode < 200 || responseCode > 299) {
                return -1;
            }
            fileLength = httpConnection.getContentLength();
            is = httpConnection.getInputStream();
            if (fileLength == -1) {

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
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {

                }
        }

        return fileLength;
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

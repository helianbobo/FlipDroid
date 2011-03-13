package flipdroid.grepper

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import groovyx.net.http.ContentType
import org.ccil.cowan.tagsoup.Parser
import org.ccil.cowan.tagsoup.AutoDetector
import groovy.util.slurpersupport.GPathResult
import java.nio.charset.Charset

class GpathFromURIFetcher {
  final autoDetectorPropertyName = 'http://www.ccil.org/~cowan/tagsoup/properties/auto-detector'
  def encodingDetector

  def fetch(String url) {
    Parser parser = new org.ccil.cowan.tagsoup.Parser()
    def detectedCharset = encodingDetector.detect(url)
    parser.setProperty(autoDetectorPropertyName, [autoDetectingReader: {inputStream ->
      new InputStreamReader(inputStream, detectedCharset)
    }
    ] as AutoDetector)

    new XmlSlurper(parser).parse(url)
  }

  def fetch(byte[] url, final Charset charSet) {
    Parser parser = new org.ccil.cowan.tagsoup.Parser()
    parser.setProperty(autoDetectorPropertyName, [autoDetectingReader: {inputStream ->
      new InputStreamReader(inputStream, charSet)
    }
    ] as AutoDetector)

    new XmlSlurper(parser).parse(new ByteArrayInputStream(url))
  }
}
namespace java it.tika
namespace py it.tika

struct TikaRequest {  
  1: string url
  2: string referencedFrom
  3: bool useCache
}

struct TikaResponse {  
  1: string title,  
  2: string content,  
  3: list <string> images, 
  4: bool success  
}  
exception TikaException {  
  1: string message  
}  
service TikaService {  
  TikaResponse fire(1:TikaRequest request) throws (1:TikaException unf)
}  
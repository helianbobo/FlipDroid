package velour.server



import javax.persistence.*;
// import com.google.appengine.api.datastore.Key;

@Entity
class Status implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id

  static constraints = {
    id visible: false
  }

  User user

  String statusId
  Date created_at
  String text
  String original_pic
  String bmiddle_pic
  String thumbnail_pic

  static embedded = ['user']
}

class User{
  String id
  String name
}

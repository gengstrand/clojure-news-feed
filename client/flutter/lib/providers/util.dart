class Util {
  static Util? _instance;
  Util._();
  static Util get instance => _instance ??= Util._();
  Map<String, String> getHeaders(String token) {
    return {
      'Authorization': 'Bearer ' + token,
    };
  }
  String getRawHost() {
    return '127.0.0.1:8080';
  }
  
  String getHost() {
    return 'http://' + getRawHost();
  }
}
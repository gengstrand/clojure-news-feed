import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import '../models/outbound.dart';
import './util.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class OutboundProvider with ChangeNotifier {
  Future<List<OutboundModel>> fetch(String token) async {
    var r = Uri.parse(Util.instance.getHost() + '/graphql?query={outbound(id:"0"){occurred,subject,story}}');
    var resp = await http.get(r, headers: Util.instance.getHeaders(token));
    if (resp.statusCode == 200) {
      notifyListeners();
      return OutboundEnvelope().build(json.decode(resp.body));
    } else {
      throw Exception("fetching outbound returned status $resp.statusCode");
    }
  }
}
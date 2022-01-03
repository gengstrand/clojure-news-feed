import 'package:flutter/material.dart';
import '../models/inbound.dart';
import './util.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class InboundProvider with ChangeNotifier {
  Future<List<InboundModel>> fetch(String token) async {
    var r = Uri.parse(Util.instance.getHost() + '/graphql?query={inbound(id:"0"){from{name},occurred,subject,story}}');
    var resp = await http.get(r, headers: Util.instance.getHeaders(token));
    if (resp.statusCode == 200) {
      return InboundEnvelope().build(json.decode(resp.body));
    } else {
      throw Exception("fetching inbound returned status $resp.statusCode");
    }
  }
}
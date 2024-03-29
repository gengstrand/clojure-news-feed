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
  Future<void> add(String token, String subject, String story) async {
    final b = json.encode({
      'subject': subject,
      'story': story,
    });
    var r = Uri.parse(Util.instance.getHost() + '/participant/outbound');
    var resp = await http.post(r, body: b, headers: Util.instance.getHeaders(token));
    if (resp.statusCode >= 400) {
      throw Exception("cannot add outboud status $resp.statusCode");
    }
  }
  Future<List<SearchResultModel>> search(String token, String keywords) async {
    var r = Uri.parse(Util.instance.getHost() + '/graphql?query={search(id:"0",keywords:"' + keywords + '"){participant{id,name,link},outbound{occurred,subject,story}}}');
    var resp = await http.get(r, headers: Util.instance.getHeaders(token));
    if (resp.statusCode == 200) {
      notifyListeners();
      return SearchResultEnvelope().build(json.decode(resp.body));
    } else {
      throw Exception("searching outbound returned status $resp.statusCode");
    }
  }
}
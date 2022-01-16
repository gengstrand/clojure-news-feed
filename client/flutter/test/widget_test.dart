import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:feed/providers/auth.dart';
import 'package:feed/providers/outbound.dart';
import 'package:feed/providers/inbound.dart';
import 'package:feed/providers/friends.dart';
import 'package:feed/providers/auth.dart';
import 'package:feed/models/outbound.dart';
import 'package:feed/models/inbound.dart';
import 'package:feed/models/participant.dart';
import 'package:feed/models/friends.dart';
import 'package:feed/screens/outbound.dart';
import 'package:feed/screens/inbound.dart';
import 'package:feed/screens/friends.dart';

final testSubject = "test subject";
final testName = "test name";

final moprv = [
  OutboundModel(
    occurred: "2022-01-15",
    subject: testSubject,
    story: "test story",
  ),
];

final miprv = [
  InboundModel(
    from: ParticipantModel(
      id: "2",
      name: "test name",
    ),
    occurred: "2022-01-15",
    subject: testSubject,
    story: "test story",
  ),
];

final mfprv = [
  FriendsModel(
    id: 1,
    from: "me",
    to: testName,
  ),
];

final cop = Completer<List<OutboundModel>>();
final cip = Completer<List<InboundModel>>();
final cfp = Completer<List<FriendsModel>>();

class MockOutboundProvider extends Mock implements OutboundProvider {
}
class MockInboundProvider extends Mock implements InboundProvider {
}
class MockFriendsProvider extends Mock implements FriendsProvider {
}
class MockAuthProvider extends Mock implements AuthProvider {
}

class MyMockedApp extends StatelessWidget {

  OutboundProvider? op;
  InboundProvider? ip;
  FriendsProvider? fp;
  AuthProvider? ap;
  final Widget screenToBeTested;
  final token = "TOKEN";
  
  MyMockedApp(this.screenToBeTested, {Key? key}) : super(key: key) {
    var mop = MockOutboundProvider();
    var mip = MockInboundProvider();
    var mfp = MockFriendsProvider();
    var map = MockAuthProvider();
    when(map.getToken()).thenReturn(token);
    when(map.getId()).thenReturn("1");
    ap = map;
    when(mop.fetch(token)).thenAnswer((_) => cop.future);
    op = mop;
    when(mip.fetch(token)).thenAnswer((_) => cip.future);
    ip = mip;
    when(mfp.fetch(token)).thenAnswer((_) => cfp.future);
    fp = mfp;
  }
  
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider<OutboundProvider>(
	  create: (_) => op,
	),
        ChangeNotifierProvider<InboundProvider>(
	  create: (_) => ip,
	),
        ChangeNotifierProvider<FriendsProvider>(
	  create: (_) => fp,
	),
        ChangeNotifierProvider<AuthProvider>(
	  create: (_) => ap,
	),
      ],
      child: MaterialApp(
        title: 'News Feed',
        theme: ThemeData(
          primarySwatch: Colors.blue,
	  cardTheme: CardTheme(
	    elevation: 2.0,
	    margin: EdgeInsets.all(5.0),
	  ),
        ),
        home: screenToBeTested,
      ),
    );
  }
}

void main() {
  testWidgets('outbound', (WidgetTester tester) async {
    await tester.pumpWidget(MyMockedApp(OutboundScreen()));
    cop.complete(moprv);
    await tester.pumpAndSettle();
    expect(find.text(testSubject), findsOneWidget);
  });
  testWidgets('inbound', (WidgetTester tester) async {
    await tester.pumpWidget(MyMockedApp(InboundScreen()));
    cip.complete(miprv);
    await tester.pumpAndSettle();
    expect(find.text(testSubject), findsOneWidget);
  });
  testWidgets('friends', (WidgetTester tester) async {
    await tester.pumpWidget(MyMockedApp(FriendsScreen()));
    cfp.complete(mfprv);
    await tester.pumpAndSettle();
    expect(find.text(testName), findsOneWidget);
  });
}

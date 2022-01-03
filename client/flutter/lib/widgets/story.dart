import 'package:flutter/material.dart';

class Story extends StatelessWidget {
  final String tale;
  Story(this.tale);
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
	Flexible(
          child: Padding(
	    padding: EdgeInsets.only(left: 20.0, bottom: 5.0),
	    child: Text(tale, textAlign: TextAlign.left),
          ),
        ),
      ],
    );
  }
}
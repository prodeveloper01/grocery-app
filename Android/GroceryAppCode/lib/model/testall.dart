import 'dart:convert';

class TestAll {
  List<DataItem> data;
  String message;
  int status;

  TestAll({
  this.data,
  this.message = "",
  this.status = 0,
  });

  TestAll.fromJson(Map<String, dynamic>  map) :
        message = map['message']  ?? "",
        status = map['status']  ?? 0;

  Map<String, dynamic> toJson() => {
        'data': data,
        'message': message,
        'status': status,
      };

  TestAll copyWith({
    List<DataItem> data,
    String message,
    int status,
  }) {
    return TestAll(
      data: data ?? this.data,
      message: message ?? this.message,
      status: status ?? this.status,
    );
  }
}


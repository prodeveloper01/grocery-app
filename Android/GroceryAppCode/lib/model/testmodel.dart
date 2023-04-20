import 'dart:convert';

class TestModel {
  List<DataItem> data;
  String message;
  int status;

  TestModel({
  this.data,
  this.message = "",
  this.status = 0,
  });

  TestModel.fromJson(Map<String, dynamic>  map) :
        message = map['message']  ?? "",
        status = map['status']  ?? 0;

  Map<String, dynamic> toJson() => {
        'data': data,
        'message': message,
        'status': status,
      };

  TestModel copyWith({
    List<DataItem> data,
    String message,
    int status,
  }) {
    return TestModel(
      data: data ?? this.data,
      message: message ?? this.message,
      status: status ?? this.status,
    );
  }
}


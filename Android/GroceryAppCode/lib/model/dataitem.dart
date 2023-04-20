import 'dart:convert';

class DataItem {
  int isAdmin;
  String image;
  String categoryName;
  int id;

  DataItem({
  this.isAdmin = 0,
  this.image = "",
  this.categoryName = "",
  this.id = 0,
  });

  DataItem.fromJson(Map<String, dynamic>  map) :
        isAdmin = map['is_admin']  ?? 0,
        image = map['image']  ?? "",
        categoryName = map['category_name']  ?? "",
        id = map['id']  ?? 0;

  Map<String, dynamic> toJson() => {
        'is_admin': isAdmin,
        'image': image,
        'category_name': categoryName,
        'id': id,
      };

  DataItem copyWith({
    int isAdmin,
    String image,
    String categoryName,
    int id,
  }) {
    return DataItem(
      isAdmin: isAdmin ?? this.isAdmin,
      image: image ?? this.image,
      categoryName: categoryName ?? this.categoryName,
      id: id ?? this.id,
    );
  }
}


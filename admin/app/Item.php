<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Item extends Model
{
    protected $table='item';
    protected $fillable=['cat_id','item_name','item_description','delivery_time'];

    public function category(){
        return $this->hasOne('App\Category','id','cat_id');
    }

    public function itemimage(){
        return $this->hasOne('App\ItemImages','item_id','id')->select('item_images.id','item_images.item_id', 'item_images.image as image_name',\DB::raw("CONCAT('".url('/public/images/item/')."/', item_images.image) AS image"));
    }

    public function variation(){
        return $this->hasMany('App\Variation','item_id','id')->select('variation.id','variation.item_id','variation.weight','variation.price','variation.stock');
    }

    public function itemimagedetails(){
        return $this->hasMany('App\ItemImages','item_id','id')->select('item_id', 'item_images.image as image_name',\DB::raw("CONCAT('".url('/public/images/item/')."/', image) AS itemimage"));
    }

    public function ingredients(){
        return $this->hasMany('App\Ingredients','item_id','id')->select('item_id',\DB::raw("CONCAT('".url('/public/images/ingredients/')."/', image) AS ingredients_image"));
    }

    public function addons(){
        return $this->hasMany('App\Addons','item_id','id')->select('id','name','price','item_id')->where('is_available','=','1');
    }
}
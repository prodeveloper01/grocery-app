<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Favorite extends Model
{
    protected $table='favorite';
    protected $fillable=['user_id','item_id'];

    public function itemimage(){
        return $this->hasOne('App\ItemImages','item_id','id')->select('id','item_id', 'item_images.image as image_name',\DB::raw("CONCAT('".url('/public/images/item/')."/', image) AS image", 'item_images.image_name'));
    }
    public function variation(){
        return $this->hasMany('App\Variation','item_id','id')->select('variation.id','variation.item_id','variation.weight','variation.price','variation.stock');
    }
}
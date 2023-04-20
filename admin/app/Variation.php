<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Variation extends Model
{
    protected $table='variation';
    protected $fillable=['item_id','price','weight','stock'];

}
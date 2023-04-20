@extends('theme.default')

@section('content')
<style type="text/css">
    .pac-container {
    z-index: 10000 !important;
}
</style>
<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Item</a></li>
        </ol>
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addProduct" data-whatever="@addProduct">Add Item</button>

        <!-- Add Item -->
        <div class="modal fade" id="addProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Add New Item</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    
                    <form id="add_product" enctype="multipart/form-data">
                    <div class="modal-body">
                        <span id="msg"></span>
                        @csrf

                        <div class="row">
                            <div class="col-md-12 ml-auto">
                                <div class="form-group">
                                    <label for="cat_id" class="col-form-label">Category:</label>
                                    <select name="cat_id" class="form-control" id="cat_id">
                                        <option value="">Select Category</option>
                                        <?php
                                        foreach ($getcategory as $category) {
                                        ?>
                                        <option value="{{$category->id}}">{{$category->category_name}}</option>
                                        <?php
                                        }
                                        ?>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-3 col-md-12">
                                <div class="form-group">
                                    <label for="item_name" class="col-form-label">Item Name:</label>
                                    <input type="text" class="form-control" name="item_name" id="item_name" placeholder="Item Name">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-3 col-md-12">
                                <div class="form-group">
                                    <label for="delivery_time" class="col-form-label">Delivery time:</label>
                                    <input type="text" class="form-control" name="delivery_time" id="delivery_time" placeholder="Delivery time">
                                </div>
                            </div>
                        </div>

                        <div class="row panel-body">

                           <div class="col-sm-3 nopadding">
                              <div class="form-group">
                                <label for="price" class="col-form-label">Price:</label>
                                <input type="text" class="form-control" id="price" name="price[]" pattern="[0-9]+" placeholder="Price" required="">
                              </div>
                           </div>
                           <div class="col-sm-4 nopadding">
                              <div class="form-group">
                                <label for="weight" class="col-form-label">Variation:</label>
                                <input type="text" class="form-control" name="weight[]" id="weight" placeholder="Variation" required="">
                              </div>
                           </div>
                           <div class="col-sm-4 nopadding">
                              <div class="form-group">
                                 <label for="stockqty" class="col-form-label">Available Stock:</label>
                                 <input type="text" class="form-control" name="stockqty[]" pattern="[0-9]+" id="stockqty" placeholder="Enter your stock qty" required="">
                              </div>
                           </div>
                           <div class="col-sm-1 nopadding">
                              <div class="form-group">
                                 <div class="input-group">
                                    <div class="input-group-btn">
                                       <button class="btn btn-success" type="button"  onclick="education_fields();"> + </button>
                                    </div>
                                 </div>
                              </div>
                           </div>
                           <div class="clear"></div>
                        </div>

                        <div id="education_fields"></div>


                        <div class="row">
                            <div class="col-sm-3 col-md-12">
                                <div class="form-group">                                    
                                    <label for="getprice" class="col-form-label">Description:</label>
                                    <textarea class="form-control" rows="3" name="description" id="description" placeholder="Product Description"></textarea>
                                </div>
                            </div>
                        </div>

                        <hr>
                        <label>Product Information</label>
                        <div class="row">
                            <div class="col-sm-3 col-md-6">
                                <div class="form-group">
                                    <label for="brand" class="col-form-label">Brand:</label>
                                    <input type="text" class="form-control" name="brand" id="brand" placeholder="Brand">
                                </div>
                            </div>

                            <div class="col-sm-3 col-md-6">
                                <div class="form-group">
                                    <label for="manufacturer" class="col-form-label">Manufacturer:</label>
                                    <input type="text" class="form-control" name="manufacturer" id="manufacturer" placeholder="Manufacturer">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-3 col-md-6">
                                <div class="form-group">
                                    <label for="country_origin" class="col-form-label">Country of Origin:</label>
                                    <input type="text" class="form-control" name="country_origin" id="country_origin" placeholder="Country of Origin">
                                </div>
                            </div>

                            <div class="col-sm-3 col-md-6">
                                <div class="form-group">
                                    <label for="ingredient_type" class="col-form-label">Ingredient Type:</label>
                                    <input type="text" class="form-control" name="ingredient_type" id="ingredient_type" placeholder="Ingredient Type">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-3 col-md-12">
                                <div class="form-group">
                                    <label for="colour" class="col-form-label">Select Item images:</label>
                                    <input type="file" multiple="true" class="form-control" name="file[]" id="file" accept=".jpg, .jpeg, .png, .mp4" required="">
                                    <input type="hidden" name="removeimg" id="removeimg">
                                </div>
                                <div class="gallery"></div>
                            </div>
                        </div>
                        
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        @if (env('Environment') == 'sendbox')
                            <button type="button" class="btn btn-primary" onclick="myFunction()">Save</button>
                        @else
                            <button type="submit" class="btn btn-primary">Save</button>
                        @endif
                    </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Item -->
        <div class="modal fade" id="EditProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabeledit" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <form method="post" name="editproduct" class="editproduct" id="editproduct" enctype="multipart/form-data">
                    @csrf
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabeledit">Edit Item</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <span id="emsg"></span>
                        <div class="modal-body">
                            <input type="hidden" class="form-control" id="id" name="id">

                            <div class="row">
                                <div class="col-md-12 ml-auto">
                                    <div class="form-group">
                                        <label for="getcat_id" class="col-form-label">Category:</label>
                                        <select name="getcat_id" class="form-control" id="getcat_id">
                                            <option value="">Select Category</option>
                                            <?php
                                            foreach ($getcategory as $category) {
                                            ?>
                                            <option value="{{$category->id}}">{{$category->category_name}}</option>
                                            <?php
                                            }
                                            ?>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-sm-3 col-md-12">
                                    <div class="form-group">
                                        <label for="getitem_name" class="col-form-label">Item Name:</label>
                                        <input type="text" class="form-control" id="getitem_name" name="item_name" placeholder="Item Name">
                                    </div>
                                </div>
                            </div>

                            <div class="row">

                                <div class="col-sm-3 col-md-12">
                                    <div class="form-group">
                                        <label for="getdelivery_time" class="col-form-label">Delivery time:</label>
                                        <input type="text" class="form-control" name="getdelivery_time" id="getdelivery_time" placeholder="Delivery time">
                                    </div>
                                </div>
                            </div>

                            <hr>
                            <label>Product Variation <button class="btn btn-success" type="button"  onclick="edititem_fields();"> + </button></label>                            

                            <div class="customer_records_dynamic"></div>

                            <div id="edititem_fields"></div>

                            <div class="row">
                                <div class="col-sm-3 col-md-12">
                                    <div class="form-group">
                                        <label for="getdescription" class="col-form-label">Description:</label>
                                        <textarea class="form-control" rows="3" name="getdescription" id="getdescription" placeholder="Product Description"></textarea>
                                    </div>
                                </div>
                            </div>

                            <label>Product Information</label>

                            <div class="row">
                                <div class="col-sm-3 col-md-6">
                                    <div class="form-group">
                                        <label for="getbrand" class="col-form-label">Brand:</label>
                                        <input type="text" class="form-control" name="getbrand" id="getbrand" placeholder="Brand">
                                    </div>
                                </div>

                                <div class="col-sm-3 col-md-6">
                                    <div class="form-group">
                                        <label for="getmanufacturer" class="col-form-label">Manufacturer:</label>
                                        <input type="text" class="form-control" name="getmanufacturer" id="getmanufacturer" placeholder="Manufacturer">
                                    </div>
                                </div>
                            </div>

                            
                            <div class="row">
                                <div class="col-sm-3 col-md-6">
                                    <div class="form-group">
                                        <label for="getcountry_origin" class="col-form-label">Country of Origin:</label>
                                        <input type="text" class="form-control" name="getcountry_origin" id="getcountry_origin" placeholder="Country of Origin">
                                    </div>
                                </div>

                                <div class="col-sm-3 col-md-6">
                                    <div class="form-group">
                                        <label for="getingredient_type" class="col-form-label">Ingredient Type:</label>
                                        <input type="text" class="form-control" name="getingredient_type" id="getingredient_type" placeholder="Ingredient Type">
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btna-secondary" data-dismiss="modal">Close</button>
                            @if (env('Environment') == 'sendbox')
                                <button type="button" class="btn btn-primary" onclick="myFunction()">Update</button>
                            @else
                                <button type="submit" class="btn btn-primary">Update</button>
                            @endif
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <span id="message"></span>
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title">All Item</h4>
                    <div class="table-responsive" id="table-display">
                        @include('theme.itemtable')
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- #/ container -->
@endsection
@section('script')
<script type="text/javascript">
    $('.table').dataTable({
      aaSorting: [[0, 'DESC']]
    });
$(document).ready(function() {
     
    $('#add_product').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        form_data.append('file',$('#file')[0].files);
        $.ajax({
            url:"{{ URL::to('admin/item/store') }}",
            method:"POST",
            data:form_data,
            cache: false,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function(result) {
                var msg = '';
                $('div.gallery').html('');  
                if(result.error.length > 0)
                {
                    msg += '<div class="alert alert-danger">'+result.error+'</div>';
                    $('#msg').html(msg);
                    setTimeout(function(){
                      $('#msg').html('');
                    }, 5000);
                }
                else
                {
                    msg += '<div class="alert alert-success mt-1">'+result.success+'</div>';
                    ProductTable();
                    $('#message').html(msg);
                    $("#addProduct").modal('hide');
                    $("#add_product")[0].reset();
                    setTimeout(function(){
                      $('#message').html('');
                    }, 5000);
                }
            },
        })
    });

    $('#editproduct').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        $.ajax({
            url:"{{ URL::to('admin/item/update') }}",
            method:'POST',
            data:form_data,
            cache: false,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function(result) {
                var msg = '';
                if(result.error.length > 0)
                {
                    for(var count = 0; count < result.error.length; count++)
                    {
                        msg += '<div class="alert alert-danger">'+result.error[count]+'</div>';
                    }
                    $('#emsg').html(msg);
                    setTimeout(function(){
                      $('#emsg').html('');
                    }, 5000);
                }
                else
                {
                    msg += '<div class="alert alert-success mt-1">'+result.success+'</div>';
                    ProductTable();
                    $('#message').html(msg);
                    $("#EditProduct").modal('hide');
                    $("#editproduct")[0].reset();
                    setTimeout(function(){
                      $('#message').html('');
                    }, 5000);
                }
            },
        });
    });
});
function GetData(id) {
    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('admin/item/show') }}",
        data: {
            id: id
        },
        method: 'POST', //Post method,
        dataType: 'json',
        success: function(response) {
            // console.log(response.ResponseData.variation);
            jQuery("#EditProduct").modal('show');
            $('#id').val(response.ResponseData.id);
            $('#getcat_id').val(response.ResponseData.cat_id);
            $('#getitem_name').val(response.ResponseData.item_name);
            $('#getdelivery_time').val(response.ResponseData.delivery_time);
            $('#getdescription').val(response.ResponseData.item_description);
            $('#getbrand').val(response.ResponseData.brand);
            $('#getmanufacturer').val(response.ResponseData.manufacturer);
            $('#getingredient_type').val(response.ResponseData.ingredient_type);
            $('#getcountry_origin').val(response.ResponseData.country_origin);
            var myHtml = "";
            $('.customer_records_dynamic').html(myHtml);
            $('#edititem_fields').html(myHtml);

            for (i = 0; i < response.ResponseData.variation.length; i++) {

                var obj = response.ResponseData.variation[i];

                var myHtml = '<div class="row panel-body" id="remove_div'+obj.id+'">'
                           + '   <div class="col-sm-3 nopadding">'
                           + '      <div class="form-group">'
                           + '          <label for="price" class="col-form-label">Price:</label>'
                           + '          <input type="text" class="form-control" id="price" name="price[]" pattern="[0-9]+" placeholder="Price" value="'+obj.price+'" required="">'
                           + '      </div>'
                           + '   </div>'
                           + '   <div class="col-sm-4 nopadding">'
                           + '      <div class="form-group">'
                           + '          <label for="weight" class="col-form-label">Variation:</label>'
                           + '          <input type="text" class="form-control" id="weight" name="weight[]" placeholder="Variation" value="'+obj.weight+'" required="">'
                           + '      </div>'
                           + '   </div>'
                           + '   <div class="col-sm-4 nopadding">'
                           + '      <div class="form-group">'
                           + '          <label for="stockqty" class="col-form-label">Available Stock:</label>'
                           + '          <input type="text" class="form-control" id="stockqty" name="stockqty[]" pattern="[0-9]+" placeholder="Available Stock" value="'+obj.stock+'" required="">'
                           + '      </div>'
                           + '   </div>'
                           + '   <div class="col-sm-1 nopadding">'
                           + '      <div class="form-group">'
                           + '          <div class="input-group">'
                           + '              <div class="input-group-btn">'
                           + '                  <button class="btn btn-danger" type="button" onclick="remove_fields('+ obj.id +');"> - </button>'
                           + '              </div>'
                           + '          </div>'
                           + '      </div>'
                           + '   </div>'
                           + '</div>';
                $('.customer_records_dynamic').append(myHtml);
            }

        },
        error: function(error) {

            // $('#errormsg').show();
        }
    })
}
function StatusUpdate(id,status) {
    swal({
        title: "Are you sure?",
        text: "Are you sure want to change the status ?",
        type: "warning",
        showCancelButton: true,
        confirmButtonClass: "btn-danger",
        confirmButtonText: "Yes, change it!",
        cancelButtonText: "No, cancel plz!",
        closeOnConfirm: false,
        closeOnCancel: false,
        showLoaderOnConfirm: true,
    },
    function(isConfirm) {
        if (isConfirm) {
            $.ajax({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                },
                url:"{{ URL::to('admin/item/status') }}",
                data: {
                    id: id,
                    status: status
                },
                method: 'POST', //Post method,
                dataType: 'json',
                success: function(response) {
                    swal({
                        title: "Approved!",
                        text: "Item status has been changed.",
                        type: "success",
                        showCancelButton: true,
                        confirmButtonClass: "btn-danger",
                        confirmButtonText: "Ok",
                        closeOnConfirm: false,
                        showLoaderOnConfirm: true,
                    },
                    function(isConfirm) {
                        if (isConfirm) {
                            swal.close();
                            ProductTable();
                        }
                    });
                },
                error: function(e) {
                    swal("Cancelled", "Something Went Wrong :(", "error");
                }
            });
        } else {
            swal("Cancelled", "Something went wrong :)", "error");
        }
    });
}

function Delete(id) {
    swal({
        title: "Are you sure?",
        text: "Are you sure want to delete ?",
        type: "warning",
        showCancelButton: true,
        confirmButtonClass: "btn-danger",
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel plz!",
        closeOnConfirm: false,
        closeOnCancel: false,
        showLoaderOnConfirm: true,
    },
    function(isConfirm) {
        if (isConfirm) {
            $.ajax({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                },
                url:"{{ URL::to('admin/item/delete') }}",
                data: {
                    id: id
                },
                method: 'POST', //Post method,
                dataType: 'json',
                success: function(response) {
                    swal({
                        title: "Approved!",
                        text: "Item has been deleted.",
                        type: "success",
                        showCancelButton: true,
                        confirmButtonClass: "btn-danger",
                        confirmButtonText: "Ok",
                        closeOnConfirm: false,
                        showLoaderOnConfirm: true,
                    },
                    function(isConfirm) {
                        if (isConfirm) {
                            swal.close();
                            ProductTable();
                        }
                    });
                },
                error: function(e) {
                    swal("Cancelled", "Something Went Wrong :(", "error");
                }
            });
        } else {
            swal("Cancelled", "Something went wrong :)", "error");
        }
    });
}

function ProductTable() {
    $.ajax({
        url:"{{ URL::to('admin/item/list') }}",
        method:'get',
        success:function(data){
            $('#table-display').html(data);
            $(".zero-configuration").DataTable({
              aaSorting: [[0, 'DESC']]
            })
        }
    });
}
 $(document).ready(function() {
     var imagesPreview = function(input, placeToInsertImagePreview) {
          if (input.files) {
              var filesAmount = input.files.length;
              $('div.gallery').html('');
              var n=0;
              for (i = 0; i < filesAmount; i++) {
                  var reader = new FileReader();
                  reader.onload = function(event) {
                       $($.parseHTML('<div>')).attr('class', 'imgdiv').attr('id','img_'+n).html('<img src="'+event.target.result+'" class="img-fluid"><span id="remove_"'+n+' onclick="removeimg('+n+')">&#x2716;</span>').appendTo(placeToInsertImagePreview); 
                      n++;
                  }
                  reader.readAsDataURL(input.files[i]);                                  
             }
          }
      };

     $('#file').on('change', function() {
         imagesPreview(this, 'div.gallery');
     });
 
});
var images = [];
function removeimg(id){
    images.push(id);
    $("#img_"+id).remove();
    $('#remove_'+id).remove();
    $('#removeimg').val(images.join(","));
}

$(".price").on("keypress keyup blur",function (event) {
    $(this).val($(this).val().replace(/[^0-9\.|\,]/g,''));
    debugger;
    if(event.which == 44)
    {
        return true;
    }
    if ((event.which != 46 || $(this).val().indexOf('.') != -1) && (event.which < 48 || event.which > 57  )) {
        event.preventDefault();
    }
});

$('#price').keyup(function(){
    var val = $(this).val();
    if(isNaN(val)){
         val = val.replace(/^[0-9]*(?:\.\d{1,2})?$/,'');
         if(val.split('.').length>2) 
             val =val.replace(/\.+$/,"");
    }
    $(this).val(val); 
});

$('#getprice').keyup(function(){
    var val = $(this).val();
    if(isNaN(val)){
         val = val.replace(/^[0-9]*(?:\.\d{1,2})?$/,'');
         if(val.split('.').length>2) 
             val =val.replace(/\.+$/,"");
    }
    $(this).val(val); 
});


$(document).on('click', '.remove-field', function(e) {
  $(this).parent('.remove').remove();
  e.preventDefault();
});

var room = 1;
function education_fields() {
 
    room++;
    var objTo = document.getElementById('education_fields')
    var divtest = document.createElement("div");
    divtest.setAttribute("class", "form-group removeclass"+room);
    var rdiv = 'removeclass'+room;
    divtest.innerHTML = '<div class="row panel-body"><div class="col-sm-3 nopadding"><div class="form-group"> <label for="price" class="col-form-label">Price:</label> <input type="text" class="form-control" id="price" name="price[]" pattern="[0-9]+" placeholder="Price" required=""></div></div><div class="col-sm-4 nopadding"><div class="form-group"> <label for="weight" class="col-form-label">Variation:</label> <input type="text" class="form-control" name="weight[]" id="weight" placeholder="Variation" required=""></div></div><div class="col-sm-4 nopadding"><div class="form-group"> <label for="stockqty" class="col-form-label">Available Stock:</label> <input type="text" class="form-control" name="stockqty[]" pattern="[0-9]+" id="stockqty" placeholder="Enter your stock qty" required=""></div></div><div class="col-sm-1 nopadding"><div class="form-group"><div class="input-group"><div class="input-group-btn"> <button class="btn btn-danger" type="button" onclick="remove_education_fields('+ room +');"> - </button></div></div></div></div><div class="clear"></div></div>';
    
    objTo.appendChild(divtest)
}
   function remove_education_fields(rid) {
       $('.removeclass'+rid).remove();
   }

var editroom = 1;
function edititem_fields() {

   editroom++;
   var editobjTo = document.getElementById('edititem_fields')
   var editdivtest = document.createElement("div");
   editdivtest.setAttribute("class", "form-group editremoveclass"+editroom);
   var rdiv = 'editremoveclass'+editroom;
   editdivtest.innerHTML = '<div class="row panel-body"><div class="col-sm-3 nopadding"><div class="form-group"> <label for="editprice" class="col-form-label">Price:</label> <input type="text" class="form-control" id="editprice" name="price[]" pattern="[0-9]+" placeholder="Price"></div></div><div class="col-sm-4 nopadding"><div class="form-group"> <label for="editweight" class="col-form-label">Weight:</label> <input type="text" class="form-control" name="weight[]" id="editweight" placeholder="Weight"></div></div><div class="col-sm-4 nopadding"><div class="form-group"> <label for="editstockqty" class="col-form-label">Available Stock:</label> <input type="text" class="form-control" name="stockqty[]" pattern="[0-9]+" id="editstockqty" placeholder="Enter your stock qty"></div></div><div class="col-sm-1 nopadding"><div class="form-group"><div class="input-group"><div class="input-group-btn"> <button class="btn btn-danger" type="button" onclick="remove_edit_fields('+ editroom +');"> - </button></div></div></div></div><div class="clear"></div></div>';
   
   editobjTo.appendChild(editdivtest)
}
function remove_edit_fields(rid) {
  $('.editremoveclass'+rid).remove();
}


function remove_fields(id){
    $('#remove_div'+id).remove();
}

</script>
@endsection
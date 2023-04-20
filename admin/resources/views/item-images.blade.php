@extends('theme.default')

@section('content')
<!-- row -->

<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Product Photos</a></li>
        </ol>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <!-- End Row -->
    <div class="row">
        <div class="col-lg-4 col-xl-3">
            <div class="card category-card">
                <div class="card-body">
                    <h4>Category</h4>
                    <p class="text-muted">{{$itemdetails->category_name}}</p>

                    <h4>Name</h4>
                    <p class="text-muted">{{$itemdetails->item_name}}</p>
                    
                    <h4>Delivery Time</h4>
                    <p>{{$itemdetails->delivery_time}}</p>

                    <h4>Description</h4>
                    <p class="text-muted">{{$itemdetails->item_description}}</p>

                    <hr>
                    
                    <h4>Brand</h4>
                    <p class="text-muted">{{$itemdetails->brand}}</p>

                    <h4>Manufacturer</h4>
                    <p>{{$itemdetails->manufacturer}}</p>

                    <h4>Country of Origin</h4>
                    <p class="text-muted">{{$itemdetails->country_origin}}</p>

                    <h4>Ingredient Type</h4>
                    <p>{{$itemdetails->ingredient_type}}</p>
                </div>
            </div>  
        </div>
        <div class="col-lg-8 col-xl-9">
            <div id="success-msg" class="alert alert-dismissible mt-3" style="display: none;">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Message!</strong> <span id="msg"></span>
            </div>
            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#AddProduct" data-whatever="@addProduct">Add Item images</button>
            <div id="card-display">
                @include('theme.itemimage')
            </div>
        </div>
    </div>
    <!-- End Row -->

    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <span id="message"></span>
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title">All Variation</h4>
                        <div class="table-responsive" id="table-display">
                            <table class="table table-striped table-bordered zero-configuration">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Weight</th>
                                        <th>Price</th>
                                        <th>Stock</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php
                                    foreach ($getvariation as $variation) {
                                    ?>
                                    <tr>
                                        <td>{{$variation->id}}</td>
                                        <td>{{$variation->weight}}</td>
                                        <td>{{Auth::user()->currency}}{{number_format($variation->price, 2)}}</td>
                                        <td>{{$variation->stock}}</td>
                                    </tr>
                                    <?php
                                    }
                                    ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Images -->
    <div class="modal fade" id="EditImages" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabeledit" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <form method="post" name="editimg" class="editimg" id="editimg" enctype="multipart/form-data">
                @csrf
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabeledit">Product Image</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div id="edit-error-msg" class="alert alert-dismissible mt-3" style="display: none;">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <strong>Message!</strong> <span id="editerrormsg"></span>
                    </div>
                    <span id="emsg"></span>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>Image</label>
                            <input type="hidden" id="idd" name="id">
                            <input type="hidden" id="item_id" name="item_id">
                            <input type="hidden" class="form-control" id="old_img" name="old_img">
                            <input type="file" class="form-control" name="image" id="image" accept=".jpg, .jpeg, .png, .mp4" required="">
                            <input type="hidden" name="removeimg" id="removeimg">
                        </div>
                        <div class="galleryim"></div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btna-secondary" data-dismiss="modal">Close</button>
                        @if (env('Environment') == 'sendbox')
                            <button type="button" class="btn btn-primary" onclick="myFunction()">Save</button>
                        @else
                            <button type="submit" class="btn btn-primary">Update</button>
                        @endif
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Add Item Image -->
    <div class="modal fade" id="AddProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <form method="post" name="addproduct" class="addproduct" id="addproduct" enctype="multipart/form-data">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Add Item Image</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        
                        <div class="form-group">
                            <label for="colour" class="col-form-label">Select Items images:</label>
                            <input type="file" multiple="true" class="form-control" name="file[]" id="file" accept=".jpg, .jpeg, .png, .mp4" required="">
                        </div>
                        <div class="gallery"></div>

                        <input type="hidden" name="itemid" id="itemid" value="{{request()->route('id')}}">
                        
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        @if (env('Environment') == 'sendbox')
                            <button type="button" class="btn btn-primary" onclick="myFunction()">Save</button>
                        @else
                            <button type="submit" name="submit" id="submit" class="btn btn-primary">Save</button>
                        @endif
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- #/ container -->

<!-- #/ container -->
@endsection
@section('script')

<script type="text/javascript">
    $(document).ready(function() {

        $('#addproduct').on('submit', function(event){
            event.preventDefault();
            var form_data = new FormData(this);
            form_data.append('file',$('#file')[0].files);
            $.ajax({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                },
                url:"{{ URL::to('admin/item/storeimages') }}",
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
                        for(var count = 0; count < result.error.length; count++)
                        {
                            msg += '<div class="alert alert-danger">'+result.error[count]+'</div>';
                        }
                        $('#msg').html(msg);
                        setTimeout(function(){
                          $('#msg').html('');
                        }, 5000);
                    }
                    else
                    {
                        msg += '<div class="alert alert-success mt-1">'+result.success+'</div>';
                        $('#message').html(msg);
                        $("#AddProduct").modal('hide');
                        $("#addproduct")[0].reset();
                        location.reload();
                    }
                },
            })
        });

        $('#editimg').on('submit', function(event){
            event.preventDefault();
            var form_data = new FormData(this);
            $.ajax({
                url:"{{ URL::to('admin/item/updateimage') }}",
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
                        location.reload();
                    }
                },
            });
        });
    });

    function EditDocument(id) {
        $.ajax({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            },
            url:"{{ URL::to('admin/item/showimage') }}",
            data: {
                id: id
            },
            method: 'POST', //Post method,
            dataType: 'json',
            success: function(response) {
                jQuery("#EditImages").modal('show');
                $('#idd').val(response.ResponseData.id);
                $('#item_id').val(response.ResponseData.item_id);
                $('.galleryim').html("<img src="+response.ResponseData.img+" class='img-fluid' style='max-height: 200px;'>");
                $('#old_img').val(response.ResponseData.image);
            },
            error: function(error) {

                // $('#errormsg').show();
            }
        })
    }

    function DeleteImage(id,item_id) {
        swal({
            title: "Are you sure?",
            text: "Do you want to delete this image?",
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
                    url:"{{ URL::to('admin/item/destroyimage') }}",
                    data:{'id':id,'item_id':item_id},
                    method: 'POST',
                    success: function(response) {
                        if (response == 1) {
                            swal({
                                title: "Approved!",
                                text: "Image has been deleted.",
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
                                    location.reload();
                                }
                            });
                        } else if (response == 2) {
                            swal("Cancelled", "You can't delete this image :(", "error");
                        } else {
                            swal("Cancelled", "Something Went Wrong :(", "error");
                        }
                    },
                    error: function(e) {
                        swal("Cancelled", "Something Went Wrong :(", "error");
                    }
                });
            } else {
                swal("Cancelled", "Your record is safe :)", "error");
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
</script>
@endsection
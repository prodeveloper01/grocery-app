@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
    <div class="container">
        <div class="row align-items-center">
            
            <div class="col-lg-12 col-md-12 col-sm-12 col-12">
                <div class="text-center">
                    <h2 class="breadcrumbs_title">My Address</h2>
                    <nav aria-label="breadcrumb">
                      <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
                        <li class="breadcrumb-item active" aria-current="page">My Address</li>
                      </ol>
                    </nav>
                </div>
            </div>
            
        </div>
    </div>
</div>
<!-- =========================== Breadcrumbs =================================== -->

<!-- =========================== My All Address =================================== -->
<section class="gray">
    <div class="container">
        <div class="row">
        
            @include('front.theme.sidebar')
            
            <div class="col-lg-8 col-md-9">
                <button type="button" class="btn btn-dark btn-sm" data-toggle="modal" data-target="#addAddress" data-whatever="@addAddress">Add Address</button>
                <!-- Order Items -->
                <div class="card style-2 mt-4">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th scope="col">Address type</th>
                                        <th scope="col">Address</th>
                                        <th scope="col">Landmark</th>
                                        <th scope="col">Door / Flat No</th>
                                        <th scope="col">Pincode</th>
                                        <th scope="col">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach ($addressdata as $address)
                                    <tr>
                                        <td>
                                            @if($address->address_type == 1)
                                                Home
                                            @elseif($address->address_type == 2)
                                                Work
                                            @elseif($address->address_type == 3)
                                                Other
                                            @endif
                                        </td>
                                        <td>{{$address->address}}</td>
                                        <td>{{$address->landmark}}</td>
                                        <td>{{$address->building}}</td>
                                        <td>{{$address->pincode}}</td>
                                        <td>
                                            <a href="#" onclick="GetData('{{$address->id}}')"> Edit </a>
                                            <a href="#" onclick="DeleteAddress('{{$address->id}}','{{Auth::user()->id}}')"> Delete </a>
                                        </td>
                                    </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>

                        <div class="row">
                            <div class="col-lg-12">
                                <nav aria-label="Page navigation example">
                                    @if ($addressdata->lastPage() > 1)
                                  <ul class="pagination">
                                    <li class="page-item left {{ ($addressdata->currentPage() == 1) ? ' disabled' : '' }}">
                                      <a class="page-link" href="{{ $addressdata->url(1) }}" aria-label="Previous">
                                        <span aria-hidden="true"><i class="ti-arrow-left mr-1"></i>Prev</span>
                                      </a>
                                    </li>
                                    @for ($i = 1; $i <= $addressdata->lastPage(); $i++)
                                    <li class="page-item {{ ($addressdata->currentPage() == $i) ? ' active' : '' }}"><a class="page-link" href="{{ $addressdata->url($i) }}">{{ $i }}</a></li>
                                    @endfor
                                    <li class="page-item right {{ ($addressdata->currentPage() == $addressdata->lastPage()) ? ' disabled' : '' }}">
                                      <a class="page-link" href="{{ $addressdata->url($addressdata->lastPage()) }}" aria-label="Previous">
                                        <span aria-hidden="true"><i class="ti-arrow-right mr-1"></i>Next</span>
                                      </a>
                                    </li>
                                  </ul>
                                  @endif
                                </nav>
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>                      
            
        </div>
    </div>

    <!-- Add Address -->
    <div class="modal fade" id="addAddress" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Add New Address</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                    </button>
                </div>
                
                <form id="add_address" method="post" action="{{ URL::to('/user/addaddress') }}">
                    <div class="modal-body">
                        @csrf
                        <label>Full Name</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="full_name" id="full_name" placeholder="Full Name">
                        </div>
                        <label>Address Type</label>
                        <div class="form-group">
                            <select class="form-control" name="address_type" id="address_type">
                                <option value="">Select Address Type</option>
                                <option value="1">Home</option>
                                <option value="2">Work</option>
                                <option value="3">Other</option>
                            </select>
                        </div>
                        <label>Address</label>
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="Delivery address" name="address" id="address" autocomplete="on">
                            <input type="hidden" id="lat" name="lat" value="0.00" />
                            <input type="hidden" id="lang" name="lang" value="0.00" />
                        </div>
                        <label>Landmark</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="landmark" id="landmark" placeholder="Landmark">
                        </div>
                        <label>Building</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="building" id="building" placeholder="Building">
                        </div>
                        <label>Mobile</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="mobile" id="mobile" placeholder="Mobile">
                        </div>
                        <label>Pincode</label>
                        <div class="form-group">
                            <select class="form-control" name="pincode" id="pincode">
                                <option value="">Select Pincode</option>
                                @foreach($getpincode as $pincode)
                                <option value="{{$pincode->pincode}}">{{$pincode->pincode}}</option>
                                @endforeach
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Address -->
    <div class="modal fade" id="EditAddress" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabelEdit" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabelEdit">Edit Address</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                    </button>
                </div>
                
                <form id="edit_address" method="post" action="{{ URL::to('/user/editaddress') }}">
                    <div class="modal-body">
                        @csrf
                        <input type="hidden" class="form-control" name="id" id="id">
                        <label>Full Name</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="full_name" id="getfull_name" placeholder="Full Name">
                        </div>
                        <label>Address Type</label>
                        <div class="form-group">
                            <select class="form-control" name="address_type" id="get_address_type">
                                <option value="">Select Address Type</option>
                                <option value="1">Home</option>
                                <option value="2">Work</option>
                                <option value="3">Other</option>
                            </select>
                        </div>
                        <label>Address</label>
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="Delivery address" name="address" id="get_address" autocomplete="on">
                            <input type="hidden" id="get_lat" name="lat" />
                            <input type="hidden" id="get_lang" name="lang" />
                        </div>
                        <label>Landmark</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="landmark" id="get_landmark" placeholder="Landmark">
                        </div>
                        <label>Building</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="building" id="get_building" placeholder="Building">
                        </div>
                        <label>Mobile</label>
                        <div class="form-group">
                            <input type="text" class="form-control" name="mobile" id="get_mobile" placeholder="Mobile">
                        </div>
                        <label>Pincode</label>
                        <div class="form-group">
                            <select class="form-control" name="pincode" id="get_pincode">
                                <option value="">Select Pincode</option>
                                @foreach($getpincode as $pincode)
                                <option value="{{$pincode->pincode}}">{{$pincode->pincode}}</option>
                                @endforeach
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Update</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>
<!-- =========================== My All Orders =================================== -->
<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDgYRuLoA_VsDsX2kgKoc3JiUnVeu085Vo&libraries=places"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.1/jquery.validate.min.js"></script>
<script>
    $(document).ready(function() {
        $( "#add_address" ).validate({
            rules :{
                full_name:{
                    required: true
                },
                address_type: {
                    required: true,
                }, 
                address: {
                    required: true,
                }, 
                landmark: {
                    required: true,
                }, 
                building: {
                    required: true,
                }, 
                mobile: {
                    required: true,
                }, 
                pincode: {
                    required: true,
                },                    
            },

        });    

        $( "#edit_address" ).validate({
            rules :{
                full_name:{
                    required: true
                },
                address_type: {
                    required: true,
                }, 
                address: {
                    required: true,
                }, 
                landmark: {
                    required: true,
                }, 
                building: {
                    required: true,
                }, 
                mobile: {
                    required: true,
                }, 
                pincode: {
                    required: true,
                },                    
            },

        });      
    });

    function initialize() {
      var input = document.getElementById('address');
      var autocomplete = new google.maps.places.Autocomplete(input);
        google.maps.event.addListener(autocomplete, 'place_changed', function () {
            var place = autocomplete.getPlace();

            for (var i = 0; i < place.address_components.length; i++) {
                var addressType = place.address_components[i].types[0];
                
                if (addressType == "administrative_area_level_1") {
                  document.getElementById("state").value = place.address_components[i].short_name;
                }

                if (addressType == "locality") {
                  document.getElementById("city").value = place.address_components[i].short_name;
                }

                // for the country, get the country code (the "short name") also
                if (addressType == "country") {
                  document.getElementById("country").value = place.address_components[i].short_name;
                }
              }

            document.getElementById('lat').value = place.geometry.location.lat();
            document.getElementById('lang').value = place.geometry.location.lng();
        });
    }
    google.maps.event.addDomListener(window, 'load', initialize);

    function addinitialize() {
      var input = document.getElementById('get_address');
      var autocomplete = new google.maps.places.Autocomplete(input);
        google.maps.event.addListener(autocomplete, 'place_changed', function () {
            var place = autocomplete.getPlace();

            for (var i = 0; i < place.address_components.length; i++) {
                var addressType = place.address_components[i].types[0];
                
                if (addressType == "administrative_area_level_1") {
                  document.getElementById("get_state").value = place.address_components[i].short_name;
                }

                if (addressType == "locality") {
                  document.getElementById("get_city").value = place.address_components[i].short_name;
                }

                // for the country, get the country code (the "short name") also
                if (addressType == "country") {
                  document.getElementById("get_country").value = place.address_components[i].short_name;
                }
              }

            document.getElementById('get_lat').value = place.geometry.location.lat();
            document.getElementById('get_lang').value = place.geometry.location.lng();
        });
    }
    google.maps.event.addDomListener(window, 'load', addinitialize);
</script>
<script type="text/javascript">
    function GetData(id) {
        $('#preloader').show();
        $.ajax({
            headers: {
                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
            },
            url:"{{ URL::to('/user/show') }}",
            data: {
                id: id
            },
            method: 'POST', //Post method,
            dataType: 'json',
            success: function(response) {
                $('#preloader').hide();
                jQuery("#EditAddress").modal('show');
                $('#id').val(response.ResponseData.id);
                $('#getfull_name').val(response.ResponseData.full_name);
                $('#get_address_type').val(response.ResponseData.address_type);
                $('#get_address').val(response.ResponseData.address);
                $('#get_lat').val(response.ResponseData.lat);
                $('#get_lang').val(response.ResponseData.lang);
                $('#get_landmark').val(response.ResponseData.landmark);
                $('#get_building').val(response.ResponseData.building);
                $('#get_mobile').val(response.ResponseData.mobile);
                $('#get_pincode').val(response.ResponseData.pincode);
            },
            error: function(error) {
                $('#preloader').hide();
            }
        })
    }
    function DeleteAddress(id,user_id) {
        swal({
            title: "Are you sure?",
            text: "Do you want to Delete this address ?",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-danger",
            confirmButtonText: "Yes, Delete it!",
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
                    url:"{{ URL::to('/user/delete') }}",
                    data: {
                        id: id,
                        user_id: user_id
                    },
                    method: 'POST',
                    success: function(response) {
                        if (response == 1) {
                            swal({
                                title: "Approved!",
                                text: "Address has been deleted.",
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
    };
</script>
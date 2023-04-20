			<!-- ============================ Footer Start ================================== -->
			<footer class="dark-footer skin-dark-footer style-2">				
				<div class="footer-middle">
					<div class="container">
						<div class="row">
							
							<div class="col-lg-4 col-md-4">
								<div class="footer_widget">
									<h4 class="extream">Contact us</h4>
									
									<div class="address_infos">
										<ul>
											<li><i class="ti-home theme-cl"></i>{{$getabout->address}}</li>
											<li><i class="ti-headphone-alt theme-cl"></i><a href="tel:{{$getabout->mobile}}">{{$getabout->mobile}}</a></li>
											<li><i class="ti-email theme-cl"></i><a href="mailto:{{$getabout->email}}">{{$getabout->email}}</a></li>
										</ul>
									</div>
									
								</div>
							</div>
									
							<div class="col-lg-3 col-md-3">
								<div class="footer_widget">
									<h4 class="widget_title">Our Company</h4>
									<ul class="footer-menu">
										<li><a href="{{URL::to('/about')}}">About Us</a></li>
										<li><a href="{{URL::to('/terms')}}">Terms & Conditions</a></li>
										<li><a href="{{URL::to('/privacy')}}">Privacy Policy</a></li>
									</ul>
								</div>
							</div>
							
							<div class="col-lg-3 col-md-3">
								<div class="footer_widget">
									<h4 class="widget_title">Useful Links</h4>
									<ul class="footer-menu">
										@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
											<li><a href="{{URL::to('/wallet')}}">My Wallet</a></li>
											<li><a href="{{URL::to('/favorite')}}">My Wishlist</a></li>
											<li><a href="{{URL::to('/account')}}">My Account</a></li>
											<li><a href="{{URL::to('/orders')}}">My Orders</a></li>
										@else
											<li><a href="{{URL::to('/signin')}}">My Wallet</a></li>
											<li><a href="{{URL::to('/signin')}}">My Wishlist</a></li>
											<li><a href="{{URL::to('/signin')}}">My Account</a></li>
											<li><a href="{{URL::to('/signin')}}">My Orders</a></li>
										@endif
									</ul>
								</div>
							</div>
							
							<div class="col-lg-2 col-md-2">
								<div class="footer_widget">
									<h4 class="widget_title">Download our App</h4>
									<ul class="footer-menu">
										@if($getabout->ios != "")
											<li><a href="{{$getabout->ios}}" target="_blank"><img src="{!! asset('public/front/img/apple-store.svg') !!}" alt="" width="120px;"></a></li>
										@endif
										@if($getabout->android != "")
											<li><a href="{{$getabout->android}}" target="_blank"><img src="{!! asset('public/front/img/play-store.png') !!}" alt="" width="120px;"></a></li>
										@endif
									</ul>
								</div>
							</div>
							
						</div>
					</div>
				</div>
				
				<div class="footer-bottom">
					<div class="container">
						<div class="row align-items-center">
							
							<div class="col-lg-6 col-md-8">
								<p class="mb-0">{{$getabout->copyright}} <br> Designed & Developed By <a href="https://infotechgravity.com" style="color: #ffc107;"><b>Gravity Infotech</b></a>.</p>
							</div>
							
							<div class="col-lg-6 col-md-6 text-right">
								<ul class="footer_social_links">
									@if($getabout->fb != "")
										<li><a href="{{$getabout->fb}}"><i class="ti-facebook" style="color: #ffc107;"></i></a></li>
									@endif
									@if($getabout->twitter != "")
										<li><a href="{{$getabout->twitter}}"><i class="ti-twitter" style="color: #ffc107;"></i></a></li>
									@endif
									@if($getabout->insta != "")
										<li><a href="{{$getabout->insta}}"><i class="ti-instagram" style="color: #ffc107;"></i></a></li>
									@endif
								</ul>
							</div>
							
						</div>
					</div>
				</div>
			</footer>
			<!-- ============================ Footer End ================================== -->
			
			<!-- cart -->
			<div id="cart-display">
				@include('front.theme.right-cart')
			</div>
			<!-- cart -->

			<!-- Left Collapse navigation -->
			<div class="w3-ch-sideBar-left w3-bar-block w3-card-2 w3-animate-right"  style="display:none;right:0;" id="leftMenu">
				<div class="rightMenu-scroll">
					<div class="flixel">
						<h4 class="cart_heading">Shop By categories</h4>
						<button onclick="closeLeftMenu()" class="w3-bar-item w3-button w3-large"><i class="ti-close"></i></button>
					</div>
					
					<div class="right-ch-sideBar">
						
						<div class="side_navigation_collapse">
							<div class="d-navigation">
								<ul id="side-menu">
									@foreach ($getcategory as $category)
										<li><a href="{{URL::to('products/'.$category->slug)}}"><span>{{$category->category_name}}</span></a></li>
									@endforeach
								</ul>
							</div>
						</div>
					</div>
					
				</div>
			</div>
			<!-- Left Collapse navigation -->
			
			<!-- Product View -->
			<div class="modal fade" id="viewproduct-over" tabindex="-1" role="dialog" aria-labelledby="add-payment" aria-hidden="true">
				<div class="modal-dialog modal-dialog-centered" role="document">
					<div class="modal-content" id="view-product">
						<span class="mod-close" data-dismiss="modal" aria-hidden="true"><i class="ti-close"></i></span>
						<div class="modal-body">
							<div class="row align-items-center">
					
						<div class="col-lg-6 col-md-12 col-sm-12">
								<div class="sp-wrap gallerys">
								</div>
							</div>
							
							<div class="col-lg-6 col-md-12 col-sm-12">
								<div class="woo_pr_detail">
									
									<div class="woo_cats_wrps">
										<a href="#" class="woo_pr_cats" id="category_name"></a>
									</div>
									<h2 class="woo_pr_title" id="item_name"></h2>
									
									<div class="woo_pr_short_desc">
										<p id="item_description"></p>
									</div>
									
									<div class="woo_pr_color flex_inline_center mb-3">
										<div class="woo_colors_list">
											<span id="variation"></span>
											{{ $errors->login->first('variation') }}
										</div>
									</div>
									
									<div class="woo_btn_action">
										@if (@Auth::user()->id != "" && @Auth::user()->type != "1")
											<input type="hidden" name="item_id" id="overview_item_id">
											<div class="col-12 col-lg-auto">
												<button class="btn btn-block btn-dark mb-2" onclick="AddtoCart('overview')">Add to Cart <i class="ti-shopping-cart-full ml-2"></i></button>
											</div>
										@else
											<div class="col-12 col-lg-auto">
												<a href="{{URL::to('/signin')}}" class="btn btn-block btn-dark mb-2">Add to Cart <i class="ti-shopping-cart-full ml-2"></i></a>
											</div>
										@endif
									</div>
									
								</div>
							</div>
							
						</div>
						</div>
					</div>
				</div>
			</div>
			<!-- End Modal -->

			<!-- Modal Add Refer-->
			<div class="modal fade text-left" id="Refer" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
			aria-hidden="true">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content">
			      <div class="modal-header">
			        <label class="modal-title text-text-bold-600" id="RditProduct">Refer and Earn</label>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
			      </div>
			      
			        <div class="modal-body">
			          	<img src='{!! asset("public/assets/images/ic_referral.png") !!}' alt="img1" border="0">
			          	<p style="color: #464648;font-size: 16px;font-weight: 500;margin-bottom: 0; text-align: center;">Share this code with a friend and you both could be eligible for <span style="color: #252c41"><b>{{$getdata->currency}}{{number_format($getdata->referral_amount, 2)}}</b></span> bonus amount under our Referral Program.</p>
			          	<hr>
			          	<div class="text-center mt-2">
				          	<label>Your Referral Code </label>
				          	<p style="color: #252c41;font-size: 35px;font-weight: 500;margin-bottom: 0; text-align: center;">{{@Auth::user()->referral_code}}</p>
			          	</div>

			          	<p style="text-align: center;">-----OR-----</p>

			          	<div class="text-center mt-2">
			          		<label>Use this link to share </label>
			          		<div class="form-group">
			          			<input type="text" class="form-control text-center" value="{{url('/signup')}}/?referral_code={{@Auth::user()->referral_code}}" id="myInput" readonly="">

			          			<div class="tooltip-refer mt-3">
			          				<button onclick="myFunction()" class="btn btn-theme" onmouseout="outFunc()">
				          			  <span class="tooltiptext" id="myTooltip">Copy to clipboard</span>
				          			  Copy link
			          			  </button>
			          			</div>
			          		</div>
			          	</div>

			        </div>
			    </div>
			  </div>
			</div>

			<!-- Modal Add Review-->
			<div class="modal fade text-left" id="AddReview" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
			aria-hidden="true">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content">
			      <div class="modal-header">
			        <label class="modal-title text-text-bold-600" id="RditProduct">Add Review</label>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
			          <span aria-hidden="true">&times;</span>
			        </button>
			      </div>
			      <div id="errorr" style="color: red;"></div>
			      
			      <form method="post" id="change_password_form">
			      {{csrf_field()}}
			        <div class="modal-body">
			          	<div class="rating"> 
				        	<input type="radio" name="rating" value="5" id="star5"><label for="star5">☆</label> 
				        	<input type="radio" name="rating" value="4" id="star4"><label for="star4">☆</label> 
				        	<input type="radio" name="rating" value="3" id="star3"><label for="star3">☆</label> 
				        	<input type="radio" name="rating" value="2" id="star2"><label for="star2">☆</label> 
				        	<input type="radio" name="rating" value="1" id="star1"><label for="star1">☆</label>
				        </div>

			          <label>Comment </label>
			          <div class="form-group">
			          	<textarea class="form-control" name="comment" id="comment" rows="5" required=""></textarea>
			          	<input type="hidden" name="user_id" id="user_id" class="form-control" value="{{@Auth::user()->id}}">
			          </div>

			        </div>
			        <div class="modal-footer">
			          <input type="button" class="btn btn-theme" onclick="addReview()"  value="Submit">
			        </div>
			      </form>
			    </div>
			  </div>
			</div>

		</div>
		<!-- ============================================================== -->
		<!-- End Wrapper -->
		<!-- ============================================================== -->

		<!-- ============================================================== -->
		<!-- All Jquery -->
		<!-- ============================================================== -->
		<script src="{!! asset('public/front/js/jquery.min.js') !!}"></script>
		<script src="{!! asset('public/front/js/popper.min.js') !!}"></script>
		<script src="{!! asset('public/front/js/bootstrap.min.js') !!}"></script>
		<script src="{!! asset('public/front/js/metisMenu.min.js') !!}"></script>
		<script src="{!! asset('public/front/js/owl-carousel.js') !!}"></script>
		<script src="{!! asset('public/front/js/ion.rangeSlider.min.js') !!}"></script>
		<script src="{!! asset('public/front/js/smoothproducts.js') !!}"></script>
		<script src="{!! asset('public/front/js/jquery-rating.js') !!}"></script>
		<script src="{!! asset('public/front/js/jQuery.style.switcher.js') !!}"></script>
		<script src="{!! asset('public/front/js/custom.js') !!}"></script>
		<script src="https://res.cloudinary.com/dxfq3iotg/raw/upload/v1557232134/toasty.js"></script>
		<script src="{!! asset('public/assets/plugins/sweetalert/js/sweetalert.min.js') !!}"></script>
		<!-- ============================================================== -->
		<!-- This page plugins -->
		<!-- ============================================================== -->

		<script>
			function openRightMenu() {
				document.getElementById("rightMenu").style.display = "block";
			}
			function closeRightMenu() {
				document.getElementById("rightMenu").style.display = "none";
			}

			function openRightMenu() {
				document.getElementById("rightMenu").style.display = "block";
			}
			function closeRightMenu() {
				document.getElementById("rightMenu").style.display = "none";
			}

            var options = {
				autoClose: true,
				progressBar: true,
				enableSounds: true,
				sounds: {

				info: "https://res.cloudinary.com/dxfq3iotg/video/upload/v1557233294/info.mp3",
				// path to sound for successfull message:
				success: "https://res.cloudinary.com/dxfq3iotg/video/upload/v1557233524/success.mp3",
				// path to sound for warn message:
				warning: "https://res.cloudinary.com/dxfq3iotg/video/upload/v1557233563/warning.mp3",
				// path to sound for error message:
				error: "https://res.cloudinary.com/dxfq3iotg/video/upload/v1557233574/error.mp3",
				},
			};

			var toast = new Toasty(options);
			toast.configure(options);
		</script>
		
		<script>
			function openLeftMenu() {
				document.getElementById("leftMenu").style.display = "block";
			}
			function closeLeftMenu() {
				document.getElementById("leftMenu").style.display = "none";
			}

			function myFunction() {

			  var copyText = document.getElementById("myInput");

			  copyText.select();

			  copyText.setSelectionRange(0, 99999);

			  document.execCommand("copy");

			  

			  var tooltip = document.getElementById("myTooltip");

			  tooltip.innerHTML = "Copied";

			}
		</script>

		<script type="text/javascript">
			function GetProductOverview(id) {
				$('#preloader').show();
				$.ajax({
				    headers: {
				        'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
				    },
				    url:"{{ URL::to('product/show') }}",
				    data: {
				        id: id
				    },
				    method: 'POST', //Post method,
				    dataType: 'json',
				    success: function(response) {
				    	$('#preloader').hide();
				        jQuery("#viewproduct-over").modal('show');
				        $('#overview_item_id').val(response.ResponseData.id);
				        $('#item_name').text(response.ResponseData.item_name);
				        $('#item_description').text(response.ResponseData.item_description);
				        $('#category_name').text(response.ResponseData.category_name);
				        $('.gallerys').html("<img src="+response.ResponseData.itemimage['image']+" class='img-fluid rounded'>");

				        let html ='';
				        for(i in response.ResponseData.variation){
				        	if (response.ResponseData.variation[i].stock == 0) {
				        		html+='<div class="custom-varient custom-size"><input disabled type="radio" class="custom-control-input"><label class="custom-control-label" style="color:#ff0000;">Out Of Stock</label></div>'
				        	} else {
				        		html+='<div class="custom-varient custom-size"><input type="radio" variation-id="'+response.ResponseData.variation[i].id+'" class="custom-control-input" name="variation" id="variation-'+i+'-'+response.ResponseData.variation[i].item_id+'" value="'+response.ResponseData.variation[i].weight+' - '+response.ResponseData.variation[i].price+'"><label class="custom-control-label" for="variation-'+i+'-'+response.ResponseData.variation[i].item_id+'">'+response.ResponseData.variation[i].weight+' - '+response.currency+''+response.ResponseData.variation[i].price+'</label></div>'
				        	}
				        }
				        $('#variation').html(html);
				    },
				    error: function(error) {
				        toast.error("Something went wrong..");
				    }
				})
			}

			function AddtoCart($type) {
				if ($type == "overview") {
					var item_id = $('#overview_item_id').val();
				} else {
					var item_id = $('#item_id').val();
				}

				var variation_id = $("input[name='variation']:checked").attr('variation-id');
				var variation = $("input[name='variation']:checked").val();

		        $('#preloader').show();
			    $.ajax({
			        headers: {
			            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
			        },
			        url:"{{ URL::to('/cart/addtocart') }}",
			        data: {
			            item_id: item_id,
			            variation: variation,
			            variation_id: variation_id
			        },
			        method: 'POST', //Post method,
			        dataType: 'json',
			        success: function(response) {
			        	$("#preloader").hide();
			            if (response.status == 1) {
			            	$('.qut_counter').text(response.cartcnt);
			                toast.success(response.message);
			                $('.view-order-btn').show();
			                $('#viewproduct-over').modal('hide');
			                RightCart();
			            } else {
			                toast.error(response.message);
			            }
			        },
			        error: function(error) {
			        	$('#preloader').hide();
			        	toast.error("Something went wrong..");
			        }
			    })
			};

			function RightCart() {
			    $.ajax({
			        url:"{{ URL::to('/cart/list') }}",
			        method:'get',
			        success:function(data){
			            $('#cart-display').html(data);
			            openRightMenu();
			        }
			    });
			}

			if ('{{request()->is("cart")}}') {
				var open = "1";
			} else {
				var open = "2";
			}
			function DeleteItem(id) {
			    swal({
			        title: "Are you sure?",
			        text: "Are you sure want to Delete this item ?",
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
			                url:"{{ URL::to('cart/delete') }}",
			                data: {
			                    id: id
			                },
			                method: 'POST',

			                success: function(response) {
			                    if (response.status == 1) {
			                        swal({
			                            title: "Approved!",
			                            text: "Item has been deleted from cart.",
			                            type: "success",
			                            showCancelButton: true,
			                            confirmButtonClass: "btn-danger",
			                            confirmButtonText: "Ok",
			                            closeOnConfirm: false,
			                            showLoaderOnConfirm: true,
			                        },
			                        function(isConfirm) {
			                            if (isConfirm) {
			                            	$('.qut_counter').text(response.cartcnt);
			                                swal.close();
			                                if (open == "1") {
			                                	location.reload();
			                                } else {
			                                	RightCart();
			                            	}
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
			}

			function qtyupdate(cart_id,type) 
			{
			    var qtys= parseInt($("#number_"+cart_id).val());
			    var cart_id= cart_id;

			    if (type == "decreaseValue") {
			        qty= qtys-1;
			    } else {
			        qty= qtys+1;
			    }

			    if (qty >= "1") {
			    	$('#preloader').show();
			        $.ajax({
			            headers: {
			                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
			            },
			            url:"{{ URL::to('cart/qtyupdate') }}",
			            data: {
			                cart_id: cart_id,
			                qty:qty,
			                type,type
			            },
			            method: 'POST',
			            success: function(response) {
			            	$('#preloader').hide();
				            if (response.status == 1) {
				                location.reload();
				            } else {
				                toast.error(response.message);
				            }
				        },
				        error: function(error) {
				        	$('#preloader').hide();
				            toast.error('Somethig went wrong');
				        }
			        });
			    } else {

			        if (qty < "1") {
			        	toast.error("You've reached the minimum units allowed for the purchase of this item");
			        } else {
			        	toast.error("You've reached the maximum units allowed for the purchase of this item");
			        }
			    }
			}

			function MakeFavorite(id,user_id) {
			    swal({
			        title: "Are you sure?",
			        text: "Do you want to add this item to Wishlist ?",
			        type: "warning",
			        showCancelButton: true,
			        confirmButtonClass: "btn-danger",
			        confirmButtonText: "Yes, add to Wishlist!",
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
			                url:"{{ URL::to('/product/favorite') }}",
			                data: {
			                    item_id: id,
			                    user_id: user_id
			                },
			                method: 'POST',
			                success: function(response) {
			                    if (response == 1) {
			                        swal({
			                            title: "Approved!",
			                            text: "Item has been added in Wishlist.",
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

			function Unfavorite(id,user_id) {
			    swal({
			        title: "Are you sure?",
			        text: "Do you want to this item from Wishlist ?",
			        type: "warning",
			        showCancelButton: true,
			        confirmButtonClass: "btn-danger",
			        confirmButtonText: "Yes, remove it!",
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
			                url:"{{ URL::to('/product/unfavorite') }}",
			                data: {
			                    item_id: id,
			                    user_id: user_id
			                },
			                method: 'POST',
			                success: function(response) {
			                    if (response == 1) {
			                        swal({
			                            title: "Approved!",
			                            text: "Item has been removed.",
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
			}

			function OrderCancel(id) {
			    swal({
			        title: "Are you sure?",
			        text: "Order amount will be transferred to your wallet",
			        type: "warning",
			        showCancelButton: true,
			        confirmButtonClass: "btn-danger",
			        confirmButtonText: "Yes, Cancel it!",
			        cancelButtonText: "No, leave plz!",
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
			                url:"{{ URL::to('/orders/ordercancel') }}",
			                data: {
		                        order_id: id,
		                    },
			                method: 'POST',
			                success: function(response) {
			                    if (response == 1) {
			                        swal({
			                            title: "Approved!",
			                            text: "Order has been cancelled.",
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

			var ratting = "";

			$('.rating input').on('click', function(){

		        ratting = $(this).val();

			});


			function addReview(){

			    var comment=$("#comment").val();
			    var user_id=$("#user_id").val();

			    var CSRF_TOKEN = $('input[name="_token"]').val();

				$('#preloader').show();
				$.ajax({
			        headers: {
			            'X-CSRF-Token': CSRF_TOKEN 
			        },
			        url:"{{ url('/addreview') }}",
			        method:'POST',
			        data: 'comment='+comment+'&ratting='+ratting+'&user_id='+user_id,
			        dataType: 'json',
			        success:function(data){
			        	$("#preloader").hide();
			            if(data.error.length > 0)
			            {
			                var error_html = '';
			                for(var count = 0; count < data.error.length; count++)
			                {
			                    error_html += '<div class="alert alert-danger mt-1">'+data.error[count]+'</div>';
			                }
			                $('#errorr').html(error_html);
			                setTimeout(function(){
			                    $('#errorr').html('');
			                }, 10000);
			            }
			            else
			            {
			                location.reload();
			            }
			        },error:function(data){
			           $('#preloader').hide();
			        }
			    });
			}

			$(document).click(function(e) {
				if (!$(e.target).is('.shop_category')) {
			    	$('.collapse').collapse('hide');	    
			    }
			});

			function contact() {
			    var firstname=$("#firstname").val();
			    var lastname=$("#lastname").val();
			    var email=$("#email").val();
			    var message=$("#message").val();
			    var CSRF_TOKEN = $('input[name="_token"]').val();
			    $('#preloader').show();
			    $.ajax({
			        headers: {
			            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
			        },
			        url:"{{ URL::to('/home/contact') }}",
			        data: {
			            firstname: firstname,
			            lastname: lastname,
			            email: email,
			            message: message
			        },
			        method: 'POST', //Post method,
			        dataType: 'json',
			        success: function(response) {
			        	$("#preloader").hide();
			            if (response.status == 1) {
			            	toast.success(response.message);
			            	$("#contactform")[0].reset();
			            } else {
			                toast.error(response.message);
			            }
			        }
			    })
			};
		</script>

	</body>
</html>
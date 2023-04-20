@include('front.theme.header')

<!-- =========================== Breadcrumbs =================================== -->
<div class="breadcrumbs_wrap dark">
    <div class="container">
        <div class="row align-items-center">
            
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div class="text-center">
                    <h2 class="breadcrumbs_title">OTP Verification</h2>
                    <nav aria-label="breadcrumb">
                      <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="{{URL::to('/')}}"><i class="ti-home"></i></a></li>
                        <li class="breadcrumb-item active" aria-current="page">OTP Verification</li>
                      </ol>
                    </nav>
                </div>
            </div>
            
        </div>
    </div>
</div>
<!-- =========================== Breadcrumbs =================================== -->


<!-- =========================== Login/Signup =================================== -->
<section>
    <div class="container">
        
        <div class="row">
            
            <div class="col-md-3"></div>
            <div class="col-lg-6 col-md-12 col-sm-12">
                @if (env('Environment') == 'sendbox')
                    <div class="alert alert-success w-100">
                        Note : This OTP is for Demo purpose.. Please click Submit to proceed
                    </div>
                @endif
                
                <div class="login_signup">
                    @if (\Session::has('danger'))
                        <div class="alert alert-danger w-100">
                            {!! \Session::get('danger') !!}
                        </div>
                    @endif
                    @if (\Session::has('success'))
                        <div class="alert alert-success w-100">
                            {!! \Session::get('success') !!}
                        </div>
                    @endif
                    <h3 class="login_sec_title">OTP Verification</h3>
                    <form id="login" action="{{ URL::to('/email-verification') }}" method="post">
                        @csrf
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" class="form-control" name="email" id="email" value="{{Session::get('email')}}" readonly="">
                            <span class="error">{{ $errors->emailverify->first('email') }}</span>
                        </div>
                        
                        <div class="form-group">
                            <label>Verification code</label>
                            @if (env('Environment') == 'sendbox')
                                <input type="text" class="form-control" name="otp" id="otp" min="1" maxlength="6" placeholder="Enter verification code" value="{{Session::get('otp')}}">
                            @else
                                <input type="text" class="form-control" name="otp" id="otp" min="1" maxlength="6" placeholder="Enter verification code">
                            @endif                            
                            <span class="error">{{ $errors->emailverify->first('otp') }}</span>
                        </div>
                        <p class="already">Didn't get an email? <span class="Btn" id="verifiBtn"></span><span id="timer"></span></p>
                        <div class="form-group">
                            <button type="submit" class="btn btn-md btn-theme col-md-12 mt-3">Submit</button>
                        </div>
                        
                    </form>
                </div>
            </div>
            
        </div>
    </div>
</section>
<!-- =========================== Login/Signup =================================== -->

<!-- ======================== Fresh Vegetables & Fruits End ==================== -->
@include('front.theme.footer')
<script>
    let timerOn = true;
    function timer(remaining) {
      var m = Math.floor(remaining / 60);
      var s = remaining % 60;
      
      m = m < 10 ? '0' + m : m;
      s = s < 10 ? '0' + s : s;
      document.getElementById('timer').innerHTML = m + ':' + s;
      remaining -= 1;
      
      if(remaining >= 0 && timerOn) {
        setTimeout(function() {
            timer(remaining);
        }, 1000);
        return;
      }
      if(!timerOn) {
        // Do validate stuff here
        return;
      }
      
      // Do timeout stuff here
      document.getElementById("verifiBtn").innerHTML = `<a href="{{URL::to('/resend-email')}}">Resend</a>`;
      document.getElementById("timer").innerHTML = "";
    }
    timer(120);
</script>
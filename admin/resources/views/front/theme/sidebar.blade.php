<div class="col-lg-4 col-md-3">

    <nav class="dashboard-nav mb-10 mb-md-0">

      <div class="list-group list-group-sm list-group-strong list-group-flush-x">

        <a class="list-group-item list-group-item-action dropright-toggle {{ request()->is('address') ? 'active' : '' }}" href="{{URL::to('/address')}}">

          My Address

        </a>

        <a class="list-group-item list-group-item-action dropright-toggle {{ request()->is('orders') ? 'active' : '' }}" href="{{URL::to('/orders')}}">

          My Orders

        </a>

        <a class="list-group-item list-group-item-action dropright-toggle {{ request()->is('account') ? 'active' : '' }}" href="{{URL::to('/account')}}">

          Account Details

        </a>

        <a class="list-group-item list-group-item-action dropright-toggle {{ request()->is('favorite') ? 'active' : '' }}" href="{{URL::to('/favorite')}}">

          My Wishlist

        </a>

        <a class="list-group-item list-group-item-action dropright-toggle" href="{{URL::to('/logout')}}" style="border-bottom: none;">

          Logout

        </a>

      </div>

    </nav>

</div>
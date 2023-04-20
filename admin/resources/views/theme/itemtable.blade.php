<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Category</th>
            <th>Product Name</th>
            <th>Variation</th>
            <th>Stock</th>
            <th>Price</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        foreach ($getitem as $item) {
            // print_r($item);
        ?>
        <tr id="dataid{{$item->id}}">
            <td>{{$item->id}}</td>
            <td>{{$item['category']->category_name}}</td>
            <td>{{$item->item_name}}</td>
            <td>
                @foreach($item->variation as $product)
                    {{ $loop->first ? '' : ', ' }}
                    {{ $product->weight }}
                @endforeach
            </td>
            <td>
                @foreach($item->variation as $product)
                    {{ $loop->first ? '' : ', ' }}
                    {{ $product->stock }}
                @endforeach
            </td>
            <td>
                @foreach($item->variation as $product)
                    {{ $loop->first ? '' : ', ' }}
                    {{Auth::user()->currency}}{{ $product->price }}
                @endforeach
            </td>
            @if (env('Environment') == 'sendbox')
                <td>
                    @if ($category->is_available == 1)
                        <a class="badge badge-success px-2" onclick="myFunction()" style="color: #fff;">Available</a>
                    @else
                        <a class="badge badge-danger px-2" onclick="myFunction()" style="color: #fff;">Unavailable</a>
                    @endif
                </td>
                <td>
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$item->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>

                        <a class="badge badge-danger px-2" onclick="myFunction()" style="color: #fff;">Delete</a>
                        
                        <a data-toggle="tooltip" href="{{URL::to('admin/item-images/'.$item->id)}}" data-original-title="View">
                            <span class="badge badge-warning">View</span>
                        </a>
                    </span>
                </td>
            @else
                <td>
                    @if ($item->item_status == 1)
                        <a class="badge badge-success px-2" onclick="StatusUpdate('{{$item->id}}','2')" style="color: #fff;">Available</a>
                    @else
                        <a class="badge badge-danger px-2" onclick="StatusUpdate('{{$item->id}}','1')" style="color: #fff;">Unavailable</a>
                    @endif
                </td>
                <td>
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$item->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>

                        <a class="badge badge-danger px-2" onclick="Delete('{{$item->id}}')" style="color: #fff;">Delete</a>

                        <a data-toggle="tooltip" href="{{URL::to('admin/item-images/'.$item->id)}}" data-original-title="View">
                            <span class="badge badge-warning">View</span>
                        </a>
                    </span>
                </td>
            @endif
        </tr>
        <?php
        }
        ?>
    </tbody>
</table>
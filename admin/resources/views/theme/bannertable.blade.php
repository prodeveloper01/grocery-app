<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Image</th>
            <th>Category</th>
            <th>Item Name</th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody id="tablecontents">
        <?php
        foreach ($getbanner as $banner) {
        ?>
        <tr class="row1" data-id="{{ $banner->id }}" id="dataid{{$banner->id}}">
            <td><i class="fa fa-sort"></i> {{$banner->order}}</td>
            <td><img src='{!! asset("public/images/banner/".$banner->image) !!}' class='img-fluid' style='max-height: 50px;'></td>
            <td>
                @if ($banner->type == "category")
                    {{@$banner['category']->category_name}}
                @else
                    --
                @endif
            </td>
            <td>
                @if ($banner->type == "item")
                    {{@$banner['item']->item_name}}
                @else
                    --
                @endif
            </td>
            <td>{{$banner->created_at}}</td>
            <td>
                @if (env('Environment') == 'sendbox')
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$banner->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="myFunction()" title="" data-original-title="Delete">
                            <span class="badge badge-danger">Delete</span>
                        </a>
                    </span>
                @else
                    <span>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$banner->id}}')" title="" data-original-title="Edit">
                            <span class="badge badge-success">Edit</span>
                        </a>
                        <a href="#" data-toggle="tooltip" data-placement="top" onclick="DeleteData('{{$banner->id}}')" title="" data-original-title="Delete">
                            <span class="badge badge-danger">Delete</span>
                        </a>
                    </span>
                @endif                
            </td>
        </tr>
        <?php
        }
        ?>
    </tbody>
</table>
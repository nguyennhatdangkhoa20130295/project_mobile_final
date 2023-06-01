package com.example.mobile_project_food_order_application;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_project_food_order_application.Common.Common;
import com.example.mobile_project_food_order_application.Database.Database;
import com.example.mobile_project_food_order_application.Model.Order;
import com.example.mobile_project_food_order_application.Model.Request;
import com.example.mobile_project_food_order_application.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;
    float totalPrice;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    static List<List<Order>> orderList = new ArrayList<>();
    static List<String> requestId = new ArrayList<>();
    static float total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        //When the "Place Order" button clicked
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        loadListFood();

    }

    //Find out whether the foods in order contains unavailable food

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Một bước nữa thôi!");
        alertDialog.setMessage("Nhập địa chỉ của bạn");
        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = edtAddress.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    // Địa chỉ rỗng, hiển thị thông báo hoặc thực hiện hành động cần thiết
                    Toast.makeText(Cart.this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
                    // Hủy dialog nếu muốn ngăn chặn đóng dialog khi địa chỉ rỗng
                    dialog.cancel();
                } else {
                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            edtAddress.getText().toString(),
                            txtTotalPrice.getText().toString(),
                            cart
                    );

                    //Submit to Firebase
                    //We will using System.Current
                    requestId.add(String.valueOf(System.currentTimeMillis()));
                    requests.child(requestId.get(requestId.size() - 1)).setValue(request);


                    //Delete the cart
                    new Database(getBaseContext()).cleanCart();

                    Toast.makeText(Cart.this, "Cảm ơn bạn vì đã đặt hàng.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        alertDialog.show();

    }


    private void loadListFood() {
        cart = new Database(this).getCarts();
        orderList.add(cart);
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        //Calculate total price
        total = 0;
        for (Order order : cart)
            total += (float) (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(numberFormat.format(total));

    }

    //Delete item

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        System.out.println(item.getTitle());
        if (item.getTitle().equals(Common.DELETE)) {
            System.out.println(item.getItemId());
            System.err.println(item.getOrder());
            deleteFoodItem(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFoodItem(int ord) {
        cart = new Database(this).getCarts();
        String order1 = cart.get(ord).getProductId();
        System.out.println("The order is " + order1);
        for (Order order : cart) {
            System.err.println("The order id is " + order.getProductId());
            if (order.getProductId().equals(order1)) {
                System.err.println(order.getProductName());
                new Database(getBaseContext()).removeFromCart(order1);
            }
        }
        loadListFood();
    }

}

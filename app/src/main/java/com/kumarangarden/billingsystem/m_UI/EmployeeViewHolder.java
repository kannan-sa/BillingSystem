package com.kumarangarden.billingsystem.m_UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kumarangarden.billingsystem.R;
import com.kumarangarden.billingsystem.m_Model.Employee;

/**
 * Created by 11000257 on 8/10/2017.
 */

public class EmployeeViewHolder extends RecyclerView.ViewHolder  {
    private TextView name, total, remaining, salary;

    public EmployeeViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.textName);
        total = (TextView) itemView.findViewById(R.id.textTotal);
        remaining = (TextView) itemView.findViewById(R.id.textDue);
        salary = (TextView) itemView.findViewById(R.id.textSalary);
    }

    public String getName() {
        return name.getText().toString();
    }

    public void Initialize(Employee employee)
    {
        name.setText(employee.GetName());
        total.setText("₹" + employee.GetTotal());
        remaining.setText("₹" + employee.GetRemaining());
        salary.setText("₹" + employee.GetSalary());
    }

}
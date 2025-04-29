package com.st10254797.smartbudgetting

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide

class ExpenseAdapter(
    private val context: Context,
    private val expenses: List<Expense>
) : BaseAdapter() {

    override fun getCount(): Int = expenses.size

    override fun getItem(position: Int): Any = expenses[position]

    override fun getItemId(position: Int): Long = expenses[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.expense_list_item, parent, false)

        val expense = expenses[position]

        val imageView = view.findViewById<ImageView>(R.id.expenseImage)
        val descriptionView = view.findViewById<TextView>(R.id.expenseDescription)
        val amountView = view.findViewById<TextView>(R.id.expenseAmount)
        val dateView = view.findViewById<TextView>(R.id.expenseDate)

        descriptionView.text = expense.description
        amountView.text = "R${expense.amount}"
        dateView.text = "Date: ${expense.date}"

        // Load image if available
        if (!expense.imageUrl.isNullOrEmpty()) {
            Glide.with(context).load(Uri.parse(expense.imageUrl)).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground) // fallback image
        }

        return view
    }
}

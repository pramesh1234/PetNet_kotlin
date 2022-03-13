package com.naestem.petnet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.naestem.petnet.R
import com.naestem.petnet.model.AddPetModel
import com.squareup.picasso.Picasso

class PetListAdapter(val context: Context) : RecyclerView.Adapter<PetListAdapter.PetViewHolder>() {
    private val petImageList = ArrayList<AddPetModel>()

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImage: ImageView = itemView.findViewById(R.id.petImageIV)
        val petName: TextView = itemView.findViewById(R.id.petNameTV)
        val petType: TextView = itemView.findViewById(R.id.petTypeTV)
        val petLocation: TextView = itemView.findViewById(R.id.petLocationTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false))
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.petName.text = petImageList[position].breed
        holder.petType.text = petImageList[position].species
        Picasso.get().load(petImageList[position].images?.get(0)).into(holder.petImage)
        holder.petLocation.text =
            "${petImageList[position].location?.subLocality}, ${petImageList[position].location?.city}"
    }

    override fun getItemCount(): Int {
        return petImageList.size
    }
    fun updatePetList(petData : AddPetModel){
        petImageList.add(petData)
        notifyDataSetChanged()
    }
}
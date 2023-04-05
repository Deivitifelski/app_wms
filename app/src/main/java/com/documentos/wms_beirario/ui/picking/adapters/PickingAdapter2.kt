package com.documentos.wms_beirario.ui.picking.adapters


//class PickingAdapter2() :
//    RecyclerView.Adapter<PickingAdapter2.PickingViewHolder2>() {
//
//    private var mListPickingResponse2: MutableList<PickingResponse2> = mutableListOf()
//
////    inner class PickingViewHolder2(val mBinding: ItemRv) :
////        RecyclerView.ViewHolder(mBinding.root) {
////        fun bind(it: PickingResponse2) {
////            with(mBinding) {
////                apiNumeroDeSeriePicking2.text = it.numeroSerie
////                apiEndVisualPicking2.text = it.enderecoVisualOrigem
////                apiPedidoPicking2.text = it.pedido
////            }
////
////
////        }
////
////    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickingViewHolder2 {
//        val mBinding =
//            ItemRvPicking2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PickingViewHolder2(mBinding)
//    }
//
//    override fun onBindViewHolder(holder: PickingViewHolder2, position: Int) {
//        holder.bind(mListPickingResponse2[position])
//    }
//
//    override fun getItemCount() = mListPickingResponse2.size
//
//    //Update adapter -->
//    fun update(it: List<PickingResponse2>) {
//        mListPickingResponse2.addAll(it)
//        notifyDataSetChanged()
////    }
//}
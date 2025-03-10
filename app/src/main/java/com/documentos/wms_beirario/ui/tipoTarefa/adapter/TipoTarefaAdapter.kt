package com.documentos.wms_beirario.ui.tipoTarefa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.documentos.wms_beirario.R
import com.documentos.wms_beirario.databinding.ItemRvArmazensBinding
import com.documentos.wms_beirario.model.tipo_tarefa.TipoTarefaResponseItem

class TipoTarefaAdapter(private val idArmazem:Int, val onClick: (TipoTarefaResponseItem) -> Unit) :
    RecyclerView.Adapter<TipoTarefaAdapter.TipoTarefaViewHolder>() {

    private var listTask = mutableListOf<TipoTarefaResponseItem>()

    inner class TipoTarefaViewHolder(val binding: ItemRvArmazensBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dados: TipoTarefaResponseItem) {
            when (dados.descricao) {
                "RECEBIMENTO" -> {
                    binding.image.setImageResource(R.drawable.recebido_1)
                }

                "RECEBIMENTO RFID" -> {
                    binding.image.setImageResource(R.drawable.icon_sensor_rfid)
                }
                "CONSULTA CÓDIGO DE BARRAS" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_qr_code_scanner_consulta_cod_barras)
                }
                "INVENTARIO" -> {
                    binding.image.setImageResource(R.drawable.inventario)
                }
                "REIMPRESSÃO DE ETIQUETAS" -> {
                    binding.image.setImageResource(R.drawable.ic_impressora_print)
                }
                "CONFIGURAÇÕES" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_settings_config)
                }
                "ARMAZENAGEM" -> {
                    binding.image.setImageResource(R.drawable.armazenagem)
                }
                "ETIQUETAGEM" -> {
                    binding.image.setImageResource(R.drawable.etiquetagem)
                }
                "PICKING" -> {
                    binding.image.setImageResource(R.drawable.picking)
                }
                "MONTAGEM" -> {
                    binding.image.setImageResource(R.drawable.montagem)
                }

                "DESMONTAGEM" -> {
                    binding.image.setImageResource(R.drawable.desmontagem)
                }

                "MOVIMENTACAO" -> {
                    binding.image.setImageResource(R.drawable.ic_baseline_arm_alt_24)
                }

                "AUDITORIA ESTOQUE" -> {
                    binding.image.setImageResource(R.drawable.auditoria)
                }

                "AUDITORIA" -> {
                    binding.image.setImageResource(R.drawable.auditoria)
                }

                "SEPARACAO" -> {
                    binding.image.setImageResource(R.drawable.separation)
                }

                "QUALIDADE" -> {
                    binding.image.setImageResource(R.drawable.quality_okok)
                }

                "RESERVA DE PEDIDO" -> {
                    binding.image.setImageResource(R.drawable.ic_reserva_pedido_24)
                }

                "CONFERÊNCIA DE EMBARQUE" -> {
                    binding.image.setImageResource(R.drawable.baseline_person_search_24_confe_embarque)
                }
            }

            with(binding) {
                txtArmazem.text =
                    dados.descricao.replace("SEPARACAO", "SEPARAÇÃO")
                        .replace("MOVIMENTACAO", "MOVIMENTAÇÃO ENTRE ENDEREÇOS")
                        .replace("MONTAGEM", "MONTAGEM DE VOLUMES")
                        .replace("INVENTARIO", " INVENTÁRIO")
                        .replace("RECEBIMENTO DE PRODUCAO", "RECEBIMENTO DE PRODUÇÃO")
                        .replace("REIMPRESS¿O", "REIMPRESSÃO")
            }
            itemView.setOnClickListener {
                onClick.invoke(dados)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoTarefaViewHolder {
        val bind = ItemRvArmazensBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipoTarefaViewHolder(bind)
    }

    override fun onBindViewHolder(holder: TipoTarefaViewHolder, position: Int) {
        holder.bind(listTask[position])
    }

    override fun getItemCount() = listTask.size


    /** itens fixos -->*/
    private fun getNewTipoTarefaArmazem() = listOf(
        TipoTarefaResponseItem("CONSULTA CÓDIGO DE BARRAS", 100, "CCB"),
        TipoTarefaResponseItem("CONFIGURAÇÕES", 101, "CONFIG"),
        TipoTarefaResponseItem("RECEBIMENTO RFID", 102, "RECRFID")
    )

    //|| Task.descricao == "CONTROLE DE QUALIDADE"

    fun update(listTask: MutableList<TipoTarefaResponseItem>) {
        /**DELETANDO TAREFAS AINDA NAO IMPLEMENTADAS -->*/
        this.listTask.clear()
        this.listTask.addAll(listTask)
        listTask.forEach { task ->
            if (task.id == 13 || task.id == 5 || task.id == 2 || task.id == 8) {
                this.listTask.remove(task)
            }
            if (idArmazem == 201 || idArmazem == 181 || idArmazem == 131){
                this.listTask.removeIf { it.id == 1 }
            }
        }
        this.listTask.addAll(getNewTipoTarefaArmazem())
        notifyDataSetChanged()
    }
}
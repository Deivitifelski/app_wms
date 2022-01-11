package com.documentos.wms_beirario.di

import TypeTaskViewModel
import com.documentos.wms_beirario.data.ServiceApi
import com.documentos.wms_beirario.repository.armazens.ArmazensRepository
import com.documentos.wms_beirario.repository.mountingvol.MountingVolRepository
import com.documentos.wms_beirario.repository.movimentacaoentreenderecos.MovimentacaoEntreEnderecosRepository
import com.documentos.wms_beirario.repository.picking.PickingRepository
import com.documentos.wms_beirario.repository.recebimento.ReceiptRepository
import com.documentos.wms_beirario.repository.receiptproduct.ReceiptProductRepository
import com.documentos.wms_beirario.repository.separacao.SeparacaoRepository
import com.documentos.wms_beirario.ui.TaskType.TypeTaskRepository
import com.documentos.wms_beirario.ui.armazens.ArmazensViewModel
import com.documentos.wms_beirario.ui.mountingVol.viewmodels.MountingVolViewModel1
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.EndMovementViewModel
import com.documentos.wms_beirario.ui.movimentacaoentreenderecos.viewmodel.ReturnTaskViewModel
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel1
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel2
import com.documentos.wms_beirario.ui.picking.viewmodel.PickingViewModel3
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.FilterReceiptProductViewModel2
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel1
import com.documentos.wms_beirario.ui.productionreceipt.viewModels.ReceiptProductViewModel2
import com.documentos.wms_beirario.ui.recebimento.ReceiptViewModel
import com.documentos.wms_beirario.ui.separacao.SeparacaoViewModel
import com.documentos.wms_beirario.ui.separacao.SeparationEndViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


/** ARMAZENS LIBERADOS AO USUARIO --> */
val mainModule = module {
    single { ServiceApi.getInstance() }
    factory {
        ArmazensRepository(serviceApi = get())
    }
    viewModel {
        ArmazensViewModel(armazensRepository = get())
    }
}

/** TIPO DE TAREFA DO ARMAZEM SELECIONADO --> */
val mainModuleTipoTarefa = module {
    factory {
        TypeTaskRepository(mServiceApi = get())
    }
    viewModel {
        TypeTaskViewModel(mRepository = get())
    }
}

/** SEPARACAO --> */
val mainModuleSeparacao = module {
    factory {
        SeparacaoRepository(mServiceApi = get())
    }
    viewModel {
        SeparacaoViewModel(mRepository = get())
    }
    viewModel {
        SeparationEndViewModel(mRepository = get())
    }
}

/**RECEBIMENTO DE PRODUÇAO -->*/
val mainModuleProduct = module {
    factory {
        ReceiptRepository(serviceApi = get())
    }
    viewModel {
        ReceiptViewModel(mReceiptRepository = get())
    }
}

/**RECEBIMENTO DE PRODUÇAO -->*/
val mainModuleReceiptProduct = module {
    factory {
        ReceiptProductRepository(service = get())
    }
    viewModel {
        ReceiptProductViewModel1(mRepository = get())
    }
    viewModel {
        ReceiptProductViewModel2(repository = get())
    }
    viewModel {
        FilterReceiptProductViewModel2(mRepository = get())
    }
}

/**PICKING -->*/
val mainModulePicking = module {
    factory {
        PickingRepository(mService = get())
    }
    viewModel {
        PickingViewModel1(mRepository = get())
    }
    viewModel {
        PickingViewModel2(mRepository = get())
    }
    viewModel {
        PickingViewModel3(mRepository = get())
    }
}

/**PICKING -->*/
val mainModuleMovimentAndress = module {
    factory {
        MovimentacaoEntreEnderecosRepository(serviceApi = get())
    }
    viewModel {
        EndMovementViewModel(repository = get())
    }
    viewModel {
        ReturnTaskViewModel(repository = get())
    }
}

/**MONTAGEM DE VOLUMES -->*/
val mainModuleMountingVol = module {
    factory {
        MountingVolRepository(mService = get())
    }
    viewModel {
        MountingVolViewModel1(mRepository = get())
    }
}




package cash.z.android.wallet.ui.presenter

import cash.z.android.wallet.ui.presenter.Presenter.PresenterView
import cash.z.android.wallet.vo.WalletTransaction
import cash.z.android.wallet.vo.WalletTransactionStatus
import cash.z.android.wallet.vo.WalletTransactionStatus.RECEIVED
import cash.z.android.wallet.vo.WalletTransactionStatus.SENT
import cash.z.wallet.sdk.data.Synchronizer
import cash.z.wallet.sdk.vo.NoteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import java.math.BigDecimal

class HomePresenter(
    private val view: HomeView,
    private val synchronizer: Synchronizer
) : Presenter {

    interface HomeView : PresenterView {
        fun addTransaction(transaction: WalletTransaction)
        fun updateBalance(old: Long, new: Long)
    }

    private lateinit var balanceJob: Job
    private lateinit var transactionJob: Job

    override suspend fun start() {
        with(view) {
            balanceJob = launchBalanceBinder(synchronizer.repository.balance())
            transactionJob = launchTransactionBinder(synchronizer.repository.transactions().map { it.toWalletTransaction() })
        }
    }

    override fun stop() {
        balanceJob.cancel()
        transactionJob.cancel()
    }

    fun CoroutineScope.launchBalanceBinder(channel: ReceiveChannel<Long>) = launch {
        var old: Long? = null
        for (model in channel) {
            bind(model, old).also { old = model }
        }
    }

    fun CoroutineScope.launchTransactionBinder(channel: ReceiveChannel<WalletTransaction>) = launch {
        for (tx in channel) {
            bind(tx)
        }
    }

    fun bind(new: Long, old: Long?) {
        view.updateBalance(new, old ?: 0L)
    }

    fun bind(transaction: WalletTransaction) {
        view.addTransaction(transaction)
    }

    private fun NoteQuery.toWalletTransaction(): WalletTransaction {
        return WalletTransaction(if (sent) SENT else RECEIVED, time, BigDecimal(value / 1e8))
    }
}

package br.gov.eletronuclear.scd.layout.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.eletronuclear.scd.data.SettingDAO
import br.gov.eletronuclear.scd.data.User
import br.gov.eletronuclear.scd.data.UserDAO
import kotlinx.coroutines.launch

class LoginViewModel(private val userDao: UserDAO, private val settingDao: SettingDAO) : ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _userLogged = MutableLiveData<User>()
    val userLogged: LiveData<User> = _userLogged


    fun loginUser(userInput: String, passwdInput: String) {
        viewModelScope.launch {
            val userLogado = userDao.findByNumber(userInput)

            if (userLogado != null) {
                if (userLogado.password == passwdInput) {
                    _loginSuccess.value = true
                    _userLogged.value = userLogado
                    settingDao.updateUserLogged(userLogado.idUser, userLogado.userName)
                } else {
                    _loginSuccess.value = false
                }
            } else {
                _loginSuccess.value = false
            }
        }
    }
}
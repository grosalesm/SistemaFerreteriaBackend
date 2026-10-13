package com.ferreteria.msauth.application.port.usecase;

import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.dto.LoginRequest;
import com.ferreteria.msauth.infrastructure.dto.LoginResponse;
import com.ferreteria.msauth.infrastructure.dto.RegistroRequest;

public interface AuthUseCase {

    LoginResponse login(LoginRequest request);

    Usuario registro(RegistroRequest request);
}

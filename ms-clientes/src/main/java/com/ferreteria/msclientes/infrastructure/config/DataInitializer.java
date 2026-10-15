package com.ferreteria.msclientes.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ferreteria.msclientes.application.port.outservice.ClienteOutService;
import com.ferreteria.msclientes.application.port.usecase.ClienteUseCase;
import com.ferreteria.msclientes.domain.bean.Cliente;
import com.ferreteria.msclientes.domain.bean.TipoCliente;
import com.ferreteria.msclientes.domain.bean.TipoDocumento;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteOutService clienteOutService;
    private final ClienteUseCase clienteUseCase;

    @Override
    public void run(String... args) {
        if (!clienteOutService.listar(null, "").isEmpty()) {
            return;
        }
        // Los dos primeros (id 1 y 2) tienen cuenta en la app: usuario y contratista (ver DataInitializer de ms-auth)
        crear(TipoDocumento.DNI, "45872136", "Luis Alberto Quispe Huamán", TipoCliente.MINORISTA,
                "987654321", "usuario@email.com", "Av. Túpac Amaru 1450, Comas");
        crear(TipoDocumento.RUC, "20548731265", "Constructora Los Andes S.A.C.", TipoCliente.CONTRATISTA,
                "014587236", "contratista@email.com", "Av. Universitaria 2365, Los Olivos");

        crear(TipoDocumento.DNI, "41236587", "María Elena Rojas Paredes", TipoCliente.MINORISTA,
                "956321478", "maria.rojas@email.com", "Jr. Los Pinos 456, San Martín de Porres");
        crear(TipoDocumento.DNI, "70125489", "José Carlos Huamán Torres", TipoCliente.MINORISTA,
                "945782361", "jose.huaman@email.com", "Calle Las Begonias 320, Independencia");
        crear(TipoDocumento.DNI, "46598712", "Rosa Isabel Flores Chávez", TipoCliente.MINORISTA,
                "912458736", "rosa.flores@email.com", "Av. Naranjal 890, Los Olivos");
        crear(TipoDocumento.DNI, "72365418", "Miguel Ángel Castillo Ramos", TipoCliente.MINORISTA,
                "998563214", "miguel.castillo@email.com", "Mz. F Lt. 12, Urb. Santa Luisa, Comas");
        crear(TipoDocumento.DNI, "43987125", "Carmen Rosa Díaz Gutiérrez", TipoCliente.MINORISTA,
                "934125678", "carmen.diaz@email.com", "Av. Canta Callao 1120, San Martín de Porres");
        crear(TipoDocumento.RUC, "10425873691", "Pedro Pablo Sánchez Vega", TipoCliente.CONTRATISTA,
                "976325418", "pedro.sanchez@email.com", "Jr. Huánuco 745, Independencia");
        crear(TipoDocumento.RUC, "20601547823", "Inversiones y Construcciones Norte E.I.R.L.", TipoCliente.CONTRATISTA,
                "015236987", "construccionesnorte@email.com", "Av. Gerardo Unger 3480, Independencia");
        crear(TipoDocumento.RUC, "20512378964", "Acabados y Remodelaciones Lima S.A.C.", TipoCliente.CONTRATISTA,
                "014789652", "acabadoslima@email.com", "Av. Alfredo Mendiola 5210, Los Olivos");
        crear(TipoDocumento.RUC, "20487569321", "Ferretería Industrial El Progreso S.R.L.", TipoCliente.MINORISTA,
                "015478963", "elprogreso@email.com", "Av. Túpac Amaru 3890, Comas");
        crear(TipoDocumento.DNI, "47852196", "Ana Lucía Mendoza Salas", TipoCliente.MINORISTA,
                "923654789", "ana.mendoza@email.com", "Calle Los Jazmines 215, Puente Piedra");
    }

    private void crear(TipoDocumento tipoDocumento, String numeroDocumento, String nombre, TipoCliente tipoCliente,
                       String telefono, String email, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(tipoDocumento);
        cliente.setNumeroDocumento(numeroDocumento);
        cliente.setNombreRazonSocial(nombre);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setDireccion(direccion);
        Cliente guardado = clienteUseCase.guardar(cliente); // se crea como MINORISTA
        if (tipoCliente == TipoCliente.CONTRATISTA) {
            clienteUseCase.cambiarTipo(guardado.getIdCliente(), TipoCliente.CONTRATISTA);
        }
    }
}

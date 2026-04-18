package views;

import data.Persistencia;
import domain.Marca;
import domain.Sucursal;
import domain.Vehiculo;
import domain.VehiculoCombustible;
import domain.VehiculoElectrico;
import domain.VehiculoTipo;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class Controlador {

    static MenuView viewMenu = new MenuView();
    static ListarVehiculosView viewList = new ListarVehiculosView();
    static AgregarVehiculoView viewAdd = new AgregarVehiculoView();

    //metodo para el menu
    public static void mostrar() {
        viewList.setVisible(false);
        viewAdd.setVisible(false);
        viewMenu.setVisible(true);
    }

    //metodo para el listado
    public static void mostrarList() {
        viewMenu.setVisible(false);
        viewList.setVisible(true);
        viewList.listarVehiculos();
    }

    //metodo para agregar
    public static void mostrarAdd() {
        viewMenu.setVisible(false);
        viewAdd.vaciarCampos();
        viewAdd.setVisible(true);
    }

    public static ArrayList<VehiculoViewModel> getVehiculos() {
        ArrayList<VehiculoViewModel> vehiculos = new ArrayList<>();
        for (Vehiculo vehiculo : Persistencia.getVehiculos()) {
            vehiculos.add(new VehiculoViewModel(vehiculo));
        }
        return vehiculos;
    }

    public static double[] calcularConsumos(Map<String, Double> vehiculos) {
        double consumoElectricos = 0;
        double consumoCombustible = 0;
        for (Map.Entry<String, Double> entry : vehiculos.entrySet()) {
            double consumo = 0;
            Optional<Vehiculo> vehiculo = Persistencia.getVehiculo(entry.getKey());
            if (vehiculo.isPresent()) {
                consumo = vehiculo.get().calcularConsumo(entry.getValue());
                consumoElectricos += vehiculo.get().esDe(VehiculoTipo.ELECTRICO) ? consumo : 0;
                consumoCombustible += vehiculo.get().esDe(VehiculoTipo.COMBUSTIBLE) ? consumo : 0;
            }
        }
        return new double[]{consumoElectricos, consumoCombustible};
    }

    public static void guardarVehiculo() {
        String patente = viewAdd.getInputPatente().getText();
        String marca = (String) viewAdd.getInputCBMarca().getSelectedItem();
        String modelo = viewAdd.getInputModelo().getText();
        int anio = Integer.parseInt(viewAdd.getInputAnio().getText());
        double capacidad = Double.parseDouble(viewAdd.getInputCapacidad().getText());
        String sucursal = (String) viewAdd.getInputCBSucursal().getSelectedItem();
        String tipo = (String) viewAdd.getInputCBTipo().getSelectedItem();
        double kwhBase = Double.parseDouble(viewAdd.getInputKwhBase().getText());
        double kpl = Double.parseDouble(viewAdd.getInputKmPorLitro().getText());
        double litrosExtra = Double.parseDouble(viewAdd.getInputLitrosExtra().getText());

        Marca selectedMarca = null;
        for (Marca mar : Persistencia.marcas) {
            if (mar.getNombre().equals(marca)) {
                selectedMarca = mar;
                break;
            }
        }

        Sucursal selectedSucursal = null;
        for (Sucursal suc : Persistencia.sucursales) {
            if (suc.getCodigo().equals(sucursal)) {
                selectedSucursal = suc;
                break;
            }
        }

        if (tipo.equals("ELECTRICO")) {
            VehiculoElectrico vehiculo = new VehiculoElectrico(patente, selectedMarca, modelo, anio, capacidad, selectedSucursal, kwhBase);
            vehiculo.setTipo(VehiculoTipo.ELECTRICO);
            Persistencia.agregarVehiculo(vehiculo);
        } else {
            VehiculoCombustible vehiculo = new VehiculoCombustible(patente, selectedMarca, modelo, anio, capacidad, selectedSucursal, kpl, litrosExtra);
            vehiculo.setTipo(VehiculoTipo.COMBUSTIBLE);
            Persistencia.agregarVehiculo(vehiculo);
        }

        mostrar();

    }
}

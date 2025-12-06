package com.gload.web;

import com.gload.model.Simulation;
import com.gload.core.storage.SimulationStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("/api/simulations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulationController {

    @Inject SimulationStorage storage;

    /**
     * 모든 Simulation 목록 조회
     */
    @GET
    public List<Simulation> listSimulations() {
        try {
            return storage.listAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException("Failed to list simulations", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulation 생성
     */
    @POST
    public Simulation createSimulation(Simulation simulation) {
        try {
            storage.save(simulation);
            return simulation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException("Failed to save simulation", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulation 조회
     */
    @GET
    @Path("/{id}")
    public Simulation getSimulation(@PathParam("id") String id) {
        try {
            Simulation sim = storage.load(id);
            if (sim == null) {
                throw new WebApplicationException("Simulation not found", Response.Status.NOT_FOUND);
            }
            return sim;
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException("Failed to load simulation", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulation 삭제
     */
    @DELETE
    @Path("/{id}")
    public Map<String, String> deleteSimulation(@PathParam("id") String id) {
        boolean deleted = storage.delete(id);
        if (deleted) {
            return Map.of("status", "deleted", "id", id);
        } else {
            throw new WebApplicationException("Failed to delete simulation", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Simulation 업데이트
     */
    @PUT
    @Path("/{id}")
    public Simulation updateSimulation(@PathParam("id") String id, Simulation simulation) {
        try {
            // ID 확인
            simulation.setId(id);
            storage.save(simulation);
            return simulation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException("Failed to update simulation", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

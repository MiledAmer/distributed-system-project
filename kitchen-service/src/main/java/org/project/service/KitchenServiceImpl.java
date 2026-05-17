package org.project.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.project.domain.TicketEntity;
import org.project.repository.TicketRepository;
import org.project.kitchen.*;
import org.project.kitchen.KitchenServiceGrpc.KitchenServiceImplBase;

import java.util.UUID;

@GrpcService
public class KitchenServiceImpl extends KitchenServiceImplBase {
        private final TicketRepository repository;

        public KitchenServiceImpl(TicketRepository repository) {
                this.repository = repository;
        }

        @Override
        public void createTicket(CreateTicketRequest req,
                        StreamObserver<CreateTicketResponse> responseObserver) {

                String id = UUID.randomUUID().toString();

                TicketEntity ticket = new TicketEntity(
                                id,
                                req.getSagaId(),
                                req.getCustomerId(),
                                "PENDING_APPROVAL");

                repository.save(ticket);

                CreateTicketResponse response = CreateTicketResponse.newBuilder()
                                .setSagaId(req.getSagaId())
                                .setTicketId(id)
                                .setCustomerId(req.getCustomerId())
                                .setStatus(TicketStatus.PENDING_APPROVAL)
                                .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
        }

        @Override
        public void acceptTicket(AcceptTicketRequest req,
                        StreamObserver<AcceptTicketResponse> res) {

                TicketEntity ticket = repository.findById(req.getTicketId())
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                ticket.setStatus("ACCEPTED");
                repository.save(ticket);

                res.onNext(AcceptTicketResponse.newBuilder()
                                .setSagaId(req.getSagaId())
                                .setTicketId(req.getTicketId())
                                .setStatus(TicketStatus.ACCEPTED)
                                .build());

                res.onCompleted();
        }

        @Override
        public void rejectTicket(RejectTicketRequest req,
                        StreamObserver<RejectTicketResponse> res) {

                TicketEntity ticket = repository.findById(req.getTicketId())
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                ticket.setStatus("REJECTED");
                repository.save(ticket);

                res.onNext(RejectTicketResponse.newBuilder()
                                .setSagaId(req.getSagaId())
                                .setTicketId(req.getTicketId())
                                .setStatus(TicketStatus.REJECTED)
                                .build());

                res.onCompleted();
        }

        @Override
        public void cancelTicket(CancelTicketRequest req,
                        StreamObserver<CancelTicketResponse> res) {

                TicketEntity ticket = repository.findById(req.getTicketId())
                                .orElseThrow(() -> new RuntimeException("Ticket not found"));

                ticket.setStatus("CANCELED");
                repository.save(ticket);

                res.onNext(CancelTicketResponse.newBuilder()
                                .setSagaId(req.getSagaId())
                                .setTicketId(req.getTicketId())
                                .setStatus(TicketStatus.CANCELED)
                                .build());

                res.onCompleted();
        }
}

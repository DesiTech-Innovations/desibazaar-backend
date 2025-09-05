package com.desitech.vyaparsathi.receiving.controller;

import com.desitech.vyaparsathi.receiving.dto.CreateReceivingDto;
import com.desitech.vyaparsathi.receiving.dto.ReceivingDto;
import com.desitech.vyaparsathi.receiving.dto.ReceivingTicketDTO;
import com.desitech.vyaparsathi.receiving.entity.Receiving;
import com.desitech.vyaparsathi.receiving.entity.ReceivingTicket;
import com.desitech.vyaparsathi.receiving.service.ReceivingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receiving")
public class ReceivingController {

    @Autowired
    private ReceivingService receivingService;

    @GetMapping
    public ResponseEntity<List<ReceivingDto>> getAllReceivings() {
        List<ReceivingDto> receivings = receivingService.getAllReceivings();
        return new ResponseEntity<>(receivings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receiving> getReceivingById(@PathVariable Long id) {
        Optional<Receiving> receivingOpt = receivingService.getReceivingById(id);
        return receivingOpt
                .map(receiving -> new ResponseEntity<>(receiving, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- Enhancement: Get Receiving by PO ID ---
    @GetMapping("/by-po/{poId}")
    public ResponseEntity<Receiving> getReceivingByPurchaseOrderId(@PathVariable Long poId) {
        Optional<Receiving> receivingOpt = receivingService.getByPurchaseOrderId(poId);
        return receivingOpt
                .map(receiving -> new ResponseEntity<>(receiving, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ReceivingDto> createReceiving(@RequestBody ReceivingDto receivingDto) {
        ReceivingDto receiving = receivingService.createReceiving(receivingDto);
        return new ResponseEntity<>(receiving, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceivingDto> updateReceiving(@PathVariable Long id, @RequestBody ReceivingDto receivingDto) {
        Optional<ReceivingDto> updatedOpt = receivingService.updateReceiving(id, receivingDto);
        return updatedOpt
                .map(updatedReceiving -> new ResponseEntity<>(updatedReceiving, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceiving(@PathVariable Long id) {
        receivingService.deleteReceiving(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/tickets")
    public ResponseEntity<ReceivingTicket> createReceivingTicket(@RequestBody ReceivingTicketDTO receivingTicketDTO) {
        ReceivingTicket ticket = receivingService.createReceivingTicket(receivingTicketDTO);
        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<ReceivingTicket> getReceivingTicketById(@PathVariable Long id) {
        Optional<ReceivingTicket> ticketOpt = receivingService.getReceivingTicketById(id);
        return ticketOpt
                .map(ticket -> new ResponseEntity<>(ticket, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/receive-goods")
    public ResponseEntity<ReceivingDto> receiveGoods(@RequestBody CreateReceivingDto createReceivingDto) {
        ReceivingDto receiving = receivingService.receiveGoods(createReceivingDto);
        return new ResponseEntity<>(receiving, HttpStatus.OK);
    }

    // TODO: Add endpoints for updating, deleting, and attaching files to receiving tickets as needed.
}
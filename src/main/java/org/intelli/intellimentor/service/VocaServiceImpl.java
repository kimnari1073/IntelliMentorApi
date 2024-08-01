package org.intelli.intellimentor.service;

import lombok.RequiredArgsConstructor;
import org.intelli.intellimentor.domain.VocaList;
import org.intelli.intellimentor.dto.VocaListDTO;
import org.intelli.intellimentor.repository.VocaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VocaServiceImpl implements VocaService{

}

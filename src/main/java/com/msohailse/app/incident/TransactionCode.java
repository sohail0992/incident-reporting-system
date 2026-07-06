package com.msohailse.app.incident;

import java.util.function.Function;
import com.msohailse.app.incident.repository.IncidentReportingRepository;

@FunctionalInterface
public interface TransactionCode<T> extends Function<IncidentReportingRepository, T> {
}

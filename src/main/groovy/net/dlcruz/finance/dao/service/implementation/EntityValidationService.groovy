package net.dlcruz.finance.dao.service.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.validation.BindException
import org.springframework.validation.DataBinder
import org.springframework.validation.FieldError
import org.springframework.validation.Validator

@Service
class EntityValidationService {

    @Autowired
    private Validator validator

    List<FieldError> validate(Object bean) {

        try {
            DataBinder dataBinder = new DataBinder(bean, bean.class.simpleName)
            dataBinder.validator = validator
            dataBinder.validate()
            dataBinder.close()
        } catch (BindException e) {
            return e.bindingResult.fieldErrors
        }

        return []
    }
}



package velour.server

class StatusController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        [ statusInstanceList: Status.list( params ), statusInstanceTotal: Status.count() ]
    }

    def show = {
        def statusInstance = Status.get( params.id )

        if(!statusInstance) {
            flash.message = "Status not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ statusInstance : statusInstance ] }
    }

    def delete = {
		Status.withTransaction {
	        def statusInstance = Status.get( params.id )
	        if(statusInstance) {
	            try {
	                statusInstance.delete(flush:true)
	                flash.message = "Status ${params.id} deleted"
	                redirect(action:list)
	            }
	            catch(org.springframework.dao.DataIntegrityViolationException e) {
	                flash.message = "Status ${params.id} could not be deleted"
	                redirect(action:show,id:params.id)
	            }
	        }
	        else {
	            flash.message = "Status not found with id ${params.id}"
	            redirect(action:list)
	        }			
		}
    }

    def edit = {
        def statusInstance = Status.get( params.id )

        if(!statusInstance) {
            flash.message = "Status not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ statusInstance : statusInstance ]
        }
    }

    def update = {
		Status.withTransaction {
	        def statusInstance = Status.get( params.id )
	        if(statusInstance) {
	            if(params.version) {
	                def version = params.version.toLong()
	                if(statusInstance.version > version) {
	                    
	                    statusInstance.errors.rejectValue("version", "status.optimistic.locking.failure", "Another user has updated this Status while you were editing.")
	                    render(view:'edit',model:[statusInstance:statusInstance])
	                    return
	                }
	            }
	            statusInstance.properties = params
	            if(!statusInstance.hasErrors() && statusInstance.save()) {
	                flash.message = "Status ${params.id} updated"
	                redirect(action:show,id:statusInstance.id)
	            }
	            else {
	                render(view:'edit',model:[statusInstance:statusInstance])
	            }
	        }
	        else {
	            flash.message = "Status not found with id ${params.id}"
	            redirect(action:list)
	        }			
		}
    }

    def create = {
        def statusInstance = new Status()
        statusInstance.properties = params
        return ['statusInstance':statusInstance]
    }

    def save = {
        def statusInstance = new Status(params)
		Status.withTransaction {
	        if(statusInstance.save(flush:true)) {
	            flash.message = "Status ${statusInstance.id} created"
	            redirect(action:show,id:statusInstance.id)
	        }
	        else {
	            render(view:'create',model:[statusInstance:statusInstance])
	        }
		}
    }
}

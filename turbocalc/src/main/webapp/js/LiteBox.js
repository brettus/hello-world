/* global Ω: false, DocumentFragment: false */

var LiteBox = function(config) {
	if(!(this instanceof LiteBox)) {
		throw "Must instantiate LiteBox with 'new LiteBox()'!";
	}
	
    var _self = this, 
        _config = config || {}, 
        _shade, 
        _wrapper1, 
        _wrapper2, 
        _wrapper3, 
        _wrapperViewport, 
        _closeButton, 
        _contentBox, 
        _oldBodyOverflow;
    
    // set defaults for values not explicitly given
    Ω.addDefaults(_config, {
        closeOnClickOut: false, 
        showCloseButton: true, 
        fadeInDelay: "0.3s", 
        // Called before closing the dialog.  If it returns false, the close is cancelled.
        onBeforeClose: function() { return true; }, 
        // Called after the dialog has closed
        onClose: function(){}, 
        dropShadow: true, 
        transparent: false, 
        border: undefined
    });
    
    var _isOpen = false;
    
    _self.setContent = function(content) {
        if(Ω.isString(content)) {
            _contentBox.innerHTML = content;
        }
        else if(content instanceof DocumentFragment || content instanceof HTMLElement) {
            _contentBox.appendChild(content);
        }
    };
    
    _self.close = function() {
        _contentBox.parentElement.removeChild(_contentBox);
        _wrapper1.parentElement.removeChild(_wrapper1);
        _wrapper2.parentElement.removeChild(_wrapper2);
        _wrapper3.parentElement.removeChild(_wrapper3);
        _wrapperViewport.parentElement.removeChild(_wrapperViewport);
        _shade.parentElement.removeChild(_shade);
        
        _contentBox = undefined;
        _wrapper1 = undefined;
        _wrapper2 = undefined;
        _wrapper3 = undefined;
        _wrapperViewport = undefined;
        _shade = undefined;
        
        Ω.body.style.overflow = _oldBodyOverflow;
        
        _isOpen = false;
    };
    
    _self.show = function(content) {        
        // grey out the background
        _shade = Ω.make("div");
        _shade.className = "LiteBoxShade";
        _shade.style.transition = "opacity " + _config.fadeInDelay + " ease";
        Ω.body.appendChild(_shade);
        
        // prevent main document from scrolling if we scroll past the litebox contents
        _oldBodyOverflow = Ω.body.style.overflow;
        Ω.body.style.overflow = "hidden";
        
        
        // CSS trickery to auto-magically center content without having to know the dimensions
        // Based on http://stackoverflow.com/a/7277724
        
        _wrapperViewport = Ω.make("div");
        _wrapperViewport.className = "LiteBoxRoot";
        
        _wrapper1 = Ω.make("span");
        _wrapper2 = Ω.make("span");
        
        _wrapper3 = Ω.make("span");
        _wrapper3.style.transition = "opacity " + _config.fadeInDelay + " ease";
        
        _contentBox = Ω.make("div");
        _contentBox.className = "contentContainer";
        if(_config.dropShadow) {
            _contentBox.className += " withDropShadow";
        }
        if(_config.transparent) {
            _contentBox.className += " transparent";
        }
        if(Ω.isString(_config.border)) {
            _contentBox.style.border = _config.border;
        }
        
        
        if(_config.showCloseButton) {
            _closeButton = Ω.make("div");
            _closeButton.appendChild(Ω.makeText("×"));
            _closeButton.className = "closeButton";
            
            Ω.on(_closeButton, "click", function(ev) {
                ev.preventDefault();
                ev.stopPropagation();
                
                if(!_config.onBeforeClose()) {
                    return;
                }
                _self.dispose();
                _config.onClose();
            });
            _wrapper3.appendChild(_closeButton);
        }
        
        _wrapper3.appendChild(_contentBox);
        _wrapper2.appendChild(_wrapper3);
        _wrapper1.appendChild(_wrapper2);
        _wrapperViewport.appendChild(_wrapper1);
        Ω.body.appendChild(_wrapperViewport);
        
        if(_config.closeOnClickOut) {
            Ω.on(_wrapperViewport, "click", function(ev) {
                if(!_contentBox.contains(ev.target)) {
                    ev.preventDefault();
                    ev.stopPropagation();
                    
                    if(!_config.onBeforeClose()) {
                        return;
                    }
                    _self.dispose();
                    _config.onClose();
                }
            });
        }
        
        _self.setContent(content);
        window.setTimeout(function() {
            _shade.style.opacity = 0.6;
            _wrapper3.style.opacity = 1;
        }, 1);
        
        _isOpen = true;
    };
    
    _self.dispose = function() {
        if(_isOpen) {
            _self.close();
        }
        _contentBox = undefined;
        _self = undefined;
    };
};
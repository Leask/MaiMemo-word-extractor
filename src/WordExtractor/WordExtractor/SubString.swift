//
//  SubString.swift
//  WordExtractor
//
//  Created by TJT on 8/17/16.
//  Copyright © 2016 TJT. All rights reserved.
//

import UIKit

class SubString: NSObject {
    
    private var start: UInt = 0
    private var end: UInt = 0
    private var source = ""
    private var hashCache: UInt32 = 0
    
    func update(str: String, start: UInt, end: UInt) {
        self.source = str
        self.start = start
        self.end = end
        hashCache = 0
    }
    
    func getStart() -> UInt {
        return start
    }
    
    func getEnd() -> UInt {
        return end
    }
    
    func length() -> UInt {
        return end - start
    }
    
    override func isEqual(object: AnyObject?) -> Bool {
        if object is SubString {
            
        }
        return false;
    }
    
    override var hash: Int {
        if (hashCache == 0 && length() > 0) {
            let scalars = source.unicodeScalars;
            for i in 0..<length() {
                hashCache = hashCache * 31 + scalars[scalars.startIndex.advancedBy(Int(i))].value;
            }
            return Int(hashCache)
        }
        return 0;
    }
    

    subscript(index: Int) -> Character {
        return source[source.startIndex.advancedBy(index)]
    }
}
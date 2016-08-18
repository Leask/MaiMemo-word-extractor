//
//  TextUtil.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "TextUtil.h"

@implementation TextUtil

+ (unichar)simpleToLower:(unichar)c {
    if (c > 64 && c <= 90) {
        return c + 32;
    }
    return c;
}

@end
